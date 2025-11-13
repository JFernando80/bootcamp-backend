package br.com.impacta.bootcamp.formacao.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.dto.CourseDTO;
import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.repository.CourseRepository;
import br.com.impacta.bootcamp.formacao.service.CourseService;
import br.com.impacta.bootcamp.formacao.specification.CourseSpecification;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        CourseSpecification msCategoria = new CourseSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<Course> msPage = courseRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(CourseDTO dto) {
        isRepetido(dto);
        courseRepository.save(montarEntity(dto));
    }

    @Override
    public void update(CourseDTO dto) {
        updateInterno(dto);
    }

    @Override
    public Course findByIdInterno(UUID id) {
        return courseRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("Course não encontrado"));
    }

    @Override
    public void delete(UUID id) {
        Course entity = findByIdInterno(id);
        courseRepository.delete(entity);
    }

    @Override
    public CourseDTO montarDTO(Course entity) {
        CourseDTO dto = new CourseDTO();
        beans.updateObjectos(dto, entity);
        dto.setPublishedAtS(beans.converterDateToString(entity.getPublishedAt()));
        dto.setCreatedAtS(beans.converterDateToString(entity.getCreatedAt()));
        dto.setUpdatedAtS(beans.converterDateToString(entity.getUpdatedAt()));

        return dto;
    }

    private Course montarEntity(CourseDTO dto) {
        Course entity = new Course();
        beans.updateObjectos(entity, dto);
        Date publishedAt = beans.converterStringToDate(dto.getPublishedAtS());
        entity.setPublishedAt(publishedAt);

        Date createdAt = beans.converterStringToDate(dto.getCreatedAtS());
        entity.setCreatedAt(createdAt);

        Date updatedAt = beans.converterStringToDate(dto.getUpdatedAtS());
        entity.setUpdatedAt(updatedAt);

        return entity ;
    }

    private void updateInterno(CourseDTO dto) {
        isRepetido(dto);
        Course entity = findByIdInterno(dto.getId());
        Course updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            courseRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(CourseDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(CourseDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("title");
        criteria.setValue(dto.getTitle());
        criteria.setOperation(SearchOperation.EQUAL.name());
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    CourseDTO dto1 = (CourseDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este course cadastrado");
            }
        }
    }
}
