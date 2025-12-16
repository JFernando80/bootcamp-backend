package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.PermissionGroupDTO;
import br.com.impacta.bootcamp.admin.model.PermissionGroup;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;

public interface PermissionGroupService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(PermissionGroupDTO dto);

    void update(PermissionGroupDTO dto);

    PermissionGroup findByIdInterno(Long id);

    void delete(Long id);

    PermissionGroupDTO montarDTO(PermissionGroup entity);

}
