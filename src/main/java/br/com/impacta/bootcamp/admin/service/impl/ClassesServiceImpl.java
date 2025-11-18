package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesDTO;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.repository.ClassesRepository;
import br.com.impacta.bootcamp.admin.service.ClassesService;
import br.com.impacta.bootcamp.admin.specification.ClassesSpecification;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClassesServiceImpl implements ClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        ClassesSpecification msCategoria = new ClassesSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<Classes> msPage = classesRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(ClassesDTO dto) {
        isRepetido(dto);
        classesRepository.save(montarEntity(dto));
    }

    @Override
    public Classes findByName(String name) {
        return classesRepository.findByName(name)
                .orElse(classesRepository.findBySimpleName(name)
                        .orElse(null));
    }

    @Override
    public void update(ClassesDTO dto) {
        updateInterno(dto);
    }

    @Override
    public Classes findByIdInterno(Long id) {
        return classesRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("Classes não encontrado"));
    }

    @Override
    public void delete(Long id) {
        Classes entity = findByIdInterno(id);
        classesRepository.delete(entity);
    }

    @Override
    public ClassesDTO montarDTO(Classes entity) {
        ClassesDTO dto = new ClassesDTO();
        beans.updateObjectos(dto, entity);
        return dto;
    }

    private Classes montarEntity(ClassesDTO dto) {
        Classes entity = new Classes();
        beans.updateObjectos(entity, dto);
        return entity ;
    }

    private void updateInterno(ClassesDTO dto) {
        isRepetido(dto);
        Classes entity = findByIdInterno(dto.getId());
        Classes updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            classesRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(ClassesDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(ClassesDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("simpleName");
        criteria.setValue(dto.getSimpleName());
        criteria.setOperation(SearchOperation.EQUAL.name());
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    ClassesDTO dto1 = (ClassesDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este classes cadastrado");
            }
        }
    }
}
