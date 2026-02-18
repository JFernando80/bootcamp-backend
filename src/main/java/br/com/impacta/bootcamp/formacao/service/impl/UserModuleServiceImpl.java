package br.com.impacta.bootcamp.formacao.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.model.UserActivity;
import br.com.impacta.bootcamp.formacao.model.UserCourse;
import br.com.impacta.bootcamp.formacao.service.ModuleService;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.dto.UserModuleDTO;
import br.com.impacta.bootcamp.formacao.model.UserModule;
import br.com.impacta.bootcamp.formacao.repository.UserModuleRepository;
import br.com.impacta.bootcamp.formacao.service.UserActivityService;
import br.com.impacta.bootcamp.formacao.service.UserCourseService;
import br.com.impacta.bootcamp.formacao.service.UserModuleService;
import br.com.impacta.bootcamp.formacao.specification.UserModuleSpecification;
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
public class UserModuleServiceImpl implements UserModuleService {

    @Autowired
    private UserModuleRepository userModuleRepository;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        UserModuleSpecification msCategoria = new UserModuleSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<UserModule> msPage = userModuleRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(UserModuleDTO dto) {
        isRepetido(dto);
        userModuleRepository.save(montarEntity(dto));
    }

    @Override
    public void update(UserModuleDTO dto) {
        updateInterno(dto);
    }

    @Override
    public UserModule findByIdInterno(UUID id) {
        return userModuleRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("UserModule não encontrado"));
    }

    @Override
    public void delete(UUID id) {
        UserModule entity = findByIdInterno(id);
        userModuleRepository.delete(entity);
    }

    @Override
    public UserModuleDTO montarDTO(UserModule entity) {
        UserModuleDTO dto = new UserModuleDTO();
        beans.updateObjectos(dto, entity);
        dto.setStartedAtS(beans.converterDateToString(entity.getStartedAt()));
        dto.setCompletedAtS(beans.converterDateToString(entity.getCompletedAt()));

        dto.setUserId(entity.getUser().getId());
        dto.setUserName(entity.getUser().getName());

        dto.setModuleId(entity.getModule().getId());
        dto.setModuleTitle(entity.getModule().getTitle());

        return dto;
    }

    @Override
    public void verificarAndFinalizarmodule(UserActivity userActivity) {
        List<UserModule> userModules = userModuleRepository.findAllByUser(userActivity.getUser());

        double total = 0D;
        double feitas = 0D;

        boolean moduleFinalizado = true;
        for (UserModule userModule : userModules) {
            List<UserActivity> userActivities = userActivityService.findAllByModule(userModule.getModule());

            total = total + userActivities.size();
            boolean finalizado = true;
            for (UserActivity activity : userActivities) {
                if (!activity.getStatus().equalsIgnoreCase("FINALIZADO")) {
                    finalizado = false;
                }

                feitas ++;
            }

            if (finalizado && !Objects.equals("FINALIZADO", userModule.getStatus())) {
                userModule.setStatus("FINALIZADO");
                userModule.setCompletedAt(new Date());
            } else if (!Objects.equals("FINALIZADO", userModule.getStatus())) {
                userModule.setStatus("CURSANDO");
            }

            if (!Objects.equals("FINALIZADO", userModule.getStatus())) {
                moduleFinalizado = false;
            }
        }

        if (moduleFinalizado) {
            userCourseService.finalizarCourse(userActivity.getModule().getCourse(), userActivity.getUser());
        } else {
            userCourseService.atualizarPercentual(userActivity.getModule().getCourse(), userActivity.getUser(), total, feitas);
        }
    }

    private UserModule montarEntity(UserModuleDTO dto) {
        UserModule entity = new UserModule();
        beans.updateObjectos(entity, dto);
        Date startedAt = beans.converterStringToDate(dto.getStartedAtS());
        entity.setStartedAt(startedAt);

        Date completedAt = beans.converterStringToDate(dto.getCompletedAtS());
        entity.setCompletedAt(completedAt);

        User user = userService.findByIdInterno(dto.getUserId());
        entity.setUser(user);

        Module module = moduleService.findByIdInterno(dto.getModuleId());
        entity.setModule(module);

        return entity ;
    }

    private void updateInterno(UserModuleDTO dto) {
        isRepetido(dto);
        UserModule entity = findByIdInterno(dto.getId());
        UserModule updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            userModuleRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(UserModuleDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(UserModuleDTO dto) {
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
        criteria.setValue(dto.getModuleId());
        criteria.setOperation(SearchOperation.EQUAL.name());
        criteria.setClasses("module");
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    UserModuleDTO dto1 = (UserModuleDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este userModule cadastrado");
            }
        }
    }
}
