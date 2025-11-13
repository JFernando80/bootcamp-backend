package br.com.impacta.bootcamp.formacao.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.dto.UserActivityDTO;
import br.com.impacta.bootcamp.formacao.model.UserActivity;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;
import java.util.UUID;

public interface UserActivityService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(UserActivityDTO dto);

    void update(UserActivityDTO dto);

    UserActivity findByIdInterno(UUID id);

    void delete(UUID id);

    UserActivityDTO montarDTO(UserActivity entity);

}
