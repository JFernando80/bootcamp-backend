package br.com.impacta.bootcamp.formacao.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.service.ModuleService;
import br.com.impacta.bootcamp.formacao.dto.ActivityDTO;
import br.com.impacta.bootcamp.formacao.model.Activity;
import br.com.impacta.bootcamp.formacao.repository.ActivityRepository;
import br.com.impacta.bootcamp.formacao.service.ActivityService;
import br.com.impacta.bootcamp.formacao.specification.ActivitySpecification;
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
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

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
        ActivitySpecification msCategoria = new ActivitySpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<Activity> msPage = activityRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(ActivityDTO dto) {
        isRepetido(dto);
        activityRepository.save(montarEntity(dto));
    }

    @Override
    public void update(ActivityDTO dto) {
        updateInterno(dto);
    }

    @Override
    public Activity findByIdInterno(UUID id) {
        return activityRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("Activity não encontrado"));
    }

    @Override
    public void delete(UUID id) {
        Activity entity = findByIdInterno(id);
        activityRepository.delete(entity);
    }

    @Override
    public ActivityDTO montarDTO(Activity entity) {
        ActivityDTO dto = new ActivityDTO();
        beans.updateObjectos(dto, entity);
        dto.setCreatedAtS(beans.converterDateToString(entity.getCreatedAt()));
        dto.setUpdatedAtS(beans.converterDateToString(entity.getUpdatedAt()));

        dto.setModuleId(entity.getModule().getId());
        dto.setModuleDescription(entity.getModule().getDescription());

        return dto;
    }

    @Override
    public List<ActivityDTO> findAllByModule(Module module) {
        return activityRepository.findAllByModule(module)
                .stream().map(this::montarDTO).toList();
    }

    private Activity montarEntity(ActivityDTO dto) {
        Activity entity = new Activity();
        beans.updateObjectos(entity, dto);
        Date createdAt = beans.converterStringToDate(dto.getCreatedAtS());
        entity.setCreatedAt(createdAt);

        Date updatedAt = beans.converterStringToDate(dto.getUpdatedAtS());
        entity.setUpdatedAt(updatedAt);

        Module module = moduleService.findByIdInterno(dto.getModuleId());
        entity.setModule(module);

        return entity ;
    }

    private void updateInterno(ActivityDTO dto) {
        isRepetido(dto);
        Activity entity = findByIdInterno(dto.getId());
        Activity updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            activityRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(ActivityDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(ActivityDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("type");
        criteria.setValue(dto.getType());
        criteria.setOperation(SearchOperation.EQUAL.name());
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
                    ActivityDTO dto1 = (ActivityDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este activity cadastrado");
            }
        }
    }
}
