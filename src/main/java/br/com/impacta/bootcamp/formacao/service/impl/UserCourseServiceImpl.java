package br.com.impacta.bootcamp.formacao.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.formacao.dto.*;
import br.com.impacta.bootcamp.formacao.model.*;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.service.*;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.repository.UserCourseRepository;
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
    private UserModuleService userModuleService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CertificateService certificateService;

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
        UserCourse entity = userCourseRepository.save(montarEntity(dto));
        criarVinculoModulo(entity);
    }

    private void criarVinculoModulo(UserCourse userCourse) {
        List<Module> modules = moduleService.findAllByCourse(userCourse.getCourse());

        for (Module module : modules) {

            UserModuleDTO userModuleDTO = new UserModuleDTO();
            userModuleDTO.setStartedAtS(beans.converterDateToString(new Date()));
            userModuleDTO.setUserName(userCourse.getUser().getName());
            userModuleDTO.setUserId(userCourse.getUser().getId());
            userModuleDTO.setModuleTitle(module.getTitle());
            userModuleDTO.setModuleId(module.getId());
            userModuleDTO.setScore(0L);
            userModuleDTO.setStatus("MATRICULADO");
            userModuleService.save(userModuleDTO);

            List<ActivityDTO> activities = activityService.findAllByModule(module);
            for (ActivityDTO activity : activities) {
                UserActivityDTO activityDTO = new UserActivityDTO();
                activityDTO.setStatus("NAO_INICIADO");
                activityDTO.setScore(0L);
                activityDTO.setUserId(userCourse.getUser().getId());
                activityDTO.setUserName(userCourse.getUser().getName());
                activityDTO.setActivityId(activity.getId());
                activityDTO.setActivityType(activity.getType());
                activityDTO.setModuleDescription(module.getDescription());
                activityDTO.setModuleId(module.getId());

                userActivityService.save(activityDTO);
            }

        }



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

    @Override
    public void atualizarPercentual(Course course, User user, double total, double feitas) {
        UserCourse userCourse = userCourseRepository.findByUserAndCourse(user, course);
        userCourse.setLastActivityAt(new Date());
        Double percentutal = feitas / total;
        userCourse.setProgressPercent(percentutal.longValue());

        updateInterno(montarDTO(userCourse));
    }

    @Override
    public void finalizarCourse(Course course, User user) {
        UserCourse userCourse = userCourseRepository.findByUserAndCourse(user, course);

        userCourse.setStatus("FINALIZADO");
        userCourse.setCertificateIssuedAt(new Date());

        Certificate certificate;
        if (userCourse.getCertificateToken() == null) {
            certificate = criarCertificado(user);
            userCourse.setCertificateToken(certificate.getToken().toString());
        }

        updateInterno(montarDTO(userCourse));
    }

    private Certificate criarCertificado(User user) {
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setData(beans.converterDateToString(new Date()));
        certificateDTO.setUserName(user.getName());
        certificateDTO.setToken(UUID.randomUUID());

        certificateService.save(certificateDTO);

        return certificateService.findByToken(certificateDTO.getToken());
    }

    private UserCourse montarEntity(UserCourseDTO dto) {
        UserCourse entity = new UserCourse();
        beans.updateObjectos(entity, dto);

        if (dto.getProgressPercent() == null) {
            entity.setProgressPercent(0L);
        }

        if (dto.getEnrolledAtS() == null) {
            entity.setEnrolledAt(new Date());
        } else {
            entity.setEnrolledAt(beans.converterStringToDate(dto.getEnrolledAtS()));
        }

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
