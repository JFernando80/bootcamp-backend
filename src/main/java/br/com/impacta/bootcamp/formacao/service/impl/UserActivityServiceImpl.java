package br.com.impacta.bootcamp.formacao.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.dto.UserActivityDTO;
import br.com.impacta.bootcamp.formacao.model.UserActivity;
import br.com.impacta.bootcamp.formacao.repository.UserActivityRepository;
import br.com.impacta.bootcamp.formacao.service.UserActivityService;
import br.com.impacta.bootcamp.formacao.specification.UserActivitySpecification;
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
public class UserActivityServiceImpl implements UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        UserActivitySpecification msCategoria = new UserActivitySpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<UserActivity> msPage = userActivityRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(UserActivityDTO dto) {
        isRepetido(dto);
        userActivityRepository.save(montarEntity(dto));
    }

    @Override
    public void update(UserActivityDTO dto) {
        updateInterno(dto);
    }

    @Override
    public UserActivity findByIdInterno(UUID id) {
        return userActivityRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("UserActivity não encontrado"));
    }

    @Override
    public void delete(UUID id) {
        UserActivity entity = findByIdInterno(id);
        userActivityRepository.delete(entity);
    }

    @Override
    public UserActivityDTO montarDTO(UserActivity entity) {
        UserActivityDTO dto = new UserActivityDTO();
        beans.updateObjectos(dto, entity);
        dto.setSubmittedAtS(beans.converterDateToString(entity.getSubmittedAt()));

        return dto;
    }

    private UserActivity montarEntity(UserActivityDTO dto) {
        UserActivity entity = new UserActivity();
        beans.updateObjectos(entity, dto);
        Date submittedAt = beans.converterStringToDate(dto.getSubmittedAtS());
        entity.setSubmittedAt(submittedAt);

        return entity ;
    }

    private void updateInterno(UserActivityDTO dto) {
        isRepetido(dto);
        UserActivity entity = findByIdInterno(dto.getId());
        UserActivity updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            userActivityRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(UserActivityDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(UserActivityDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    UserActivityDTO dto1 = (UserActivityDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este userActivity cadastrado");
            }
        }
    }
}
