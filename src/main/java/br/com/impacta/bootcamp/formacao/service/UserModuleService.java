package br.com.impacta.bootcamp.formacao.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.dto.UserModuleDTO;
import br.com.impacta.bootcamp.formacao.model.UserModule;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;
import java.util.UUID;

public interface UserModuleService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(UserModuleDTO dto);

    void update(UserModuleDTO dto);

    UserModule findByIdInterno(UUID id);

    void delete(UUID id);

    UserModuleDTO montarDTO(UserModule entity);

}
