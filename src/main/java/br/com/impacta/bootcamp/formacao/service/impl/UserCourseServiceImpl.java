package br.com.impacta.bootcamp.formacao.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.service.CourseService;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.dto.UserCourseDTO;
import br.com.impacta.bootcamp.formacao.model.UserCourse;
import br.com.impacta.bootcamp.formacao.repository.UserCourseRepository;
import br.com.impacta.bootcamp.formacao.service.UserCourseService;
import br.com.impacta.bootcamp.formacao.specification.UserCourseSpecification;
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
public class UserCourseServiceImpl implements UserCourseService {

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        UserCourseSpecification msCategoria = new UserCourseSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<UserCourse> msPage = userCourseRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(UserCourseDTO dto) {
        isRepetido(dto);
        userCourseRepository.save(montarEntity(dto));
    }

    @Override
    public void update(UserCourseDTO dto) {
        updateInterno(dto);
    }

    @Override
    public UserCourse findByIdInterno(UUID id) {
        return userCourseRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("UserCourse não encontrado"));
    }

    @Override
    public void delete(UUID id) {
        UserCourse entity = findByIdInterno(id);
        userCourseRepository.delete(entity);
    }

    @Override
    public UserCourseDTO montarDTO(UserCourse entity) {
        UserCourseDTO dto = new UserCourseDTO();
        beans.updateObjectos(dto, entity);
        dto.setEnrolledAtS(beans.converterDateToString(entity.getEnrolledAt()));
        dto.setCertificateIssuedAtS(beans.converterDateToString(entity.getCertificateIssuedAt()));
        dto.setLastActivityAtS(beans.converterDateToString(entity.getLastActivityAt()));

        dto.setUserId(entity.getUser().getId());
        dto.setUserName(entity.getUser().getName());

        dto.setCourseId(entity.getCourse().getId());
        dto.setCourseDescription(entity.getCourse().getDescription());

        return dto;
    }

    private UserCourse montarEntity(UserCourseDTO dto) {
        UserCourse entity = new UserCourse();
        beans.updateObjectos(entity, dto);
        Date enrolledAt = beans.converterStringToDate(dto.getEnrolledAtS());
        entity.setEnrolledAt(enrolledAt);

        Date certificateIssuedAt = beans.converterStringToDate(dto.getCertificateIssuedAtS());
        entity.setCertificateIssuedAt(certificateIssuedAt);

        Date lastActivityAt = beans.converterStringToDate(dto.getLastActivityAtS());
        entity.setLastActivityAt(lastActivityAt);

        User user = userService.findByIdInterno(dto.getUserId());
        entity.setUser(user);

        Course course = courseService.findByIdInterno(dto.getCourseId());
        entity.setCourse(course);

        return entity ;
    }

    private void updateInterno(UserCourseDTO dto) {
        isRepetido(dto);
        UserCourse entity = findByIdInterno(dto.getId());
        UserCourse updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            userCourseRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(UserCourseDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(UserCourseDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("id");
        criteria.setValue(dto.getUserId());
        criteria.setOperation(SearchOperation.EQUAL.name());
        criteria.setClasses("user");
        lista.add(criteria);

        criteria = new SearchCriteriaDTO();
        criteria.setKey("id");
        criteria.setValue(dto.getCourseId());
        criteria.setOperation(SearchOperation.EQUAL.name());
        criteria.setClasses("course");
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    UserCourseDTO dto1 = (UserCourseDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este userCourse cadastrado");
            }
        }
    }
}
