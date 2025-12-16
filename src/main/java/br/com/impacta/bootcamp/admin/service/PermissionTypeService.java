package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.PermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;

public interface PermissionTypeService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(PermissionTypeDTO dto);

    void update(PermissionTypeDTO dto);

    PermissionType findByIdInterno(Long id);

    void delete(Long id);

    PermissionTypeDTO montarDTO(PermissionType entity);

}
