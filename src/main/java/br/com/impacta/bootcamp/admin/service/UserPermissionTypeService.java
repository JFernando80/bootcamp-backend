package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.UserPermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.model.UserPermissionType;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;

public interface UserPermissionTypeService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(UserPermissionTypeDTO dto);

    void update(UserPermissionTypeDTO dto);

    UserPermissionType findByIdInterno(Long id);

    void delete(Long id);

    UserPermissionTypeDTO montarDTO(UserPermissionType entity);

    List<UserPermissionType> findAllByUserInterno(User user);
}
