package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.PerfilPermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;

public interface PerfilPermissionTypeService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(PerfilPermissionTypeDTO dto);

    void update(PerfilPermissionTypeDTO dto);

    PerfilPermissionType findByIdInterno(Long id);

    void delete(Long id);

    PerfilPermissionTypeDTO montarDTO(PerfilPermissionType entity);

    List<PerfilPermissionType> listFromTypePermissionInterno(PermissionType permissionType);
}
