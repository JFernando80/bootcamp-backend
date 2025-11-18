package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.PermissionDTO;
import br.com.impacta.bootcamp.admin.model.Permission;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;

public interface PermissionService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    List<Permission> findAllInterno();

    void save(PermissionDTO dto);

    void update(PermissionDTO dto);

    Permission findByIdInterno(Long id);

    PermissionDTO findBySerial(Long serial);

    void delete(Long id);

    PermissionDTO montarDTO(Permission entity);

}
