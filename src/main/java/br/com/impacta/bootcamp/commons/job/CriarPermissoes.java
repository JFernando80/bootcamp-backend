package br.com.impacta.bootcamp.commons.job;

import br.com.impacta.bootcamp.BootcampApplication;
import br.com.impacta.bootcamp.admin.dto.*;
import br.com.impacta.bootcamp.admin.model.*;
import br.com.impacta.bootcamp.admin.service.*;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.enums.PermissoesEnum;
import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import br.com.impacta.bootcamp.commons.util.Monitorar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class CriarPermissoes {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionGroupService permissionGroupService;

    @Autowired
    private PermissionTypeService permissionTypeService;

    @Autowired
    private PerfilPermissionTypeService perfilPermissionTypeService;

    @Autowired
    private UserPermissionTypeService userPermissionTypeService;

    @Monitorar
    @Scheduled(fixedDelay = 600000000, initialDelay = 500)
    public void automatizaCalendario() {
        if (BootcampApplication.executouPermissoes) {
            return;
        }

        for (int i = 0; i < PermissoesEnum.values().length; i++) {
            PermissoesEnum perm = PermissoesEnum.values()[i];
            PermissionDTO permission = permissionService.findBySerial(perm.getSerial());

            if (Objects.isNull(permission)) {
                permission = new PermissionDTO();
                permission.setId(perm.getId());
            }

            permission.setPermission(perm.getPermission());
            permission.setPermissionDescription(perm.getPermissionDescription());
            permission.setScreen(perm.getScreen());
            permission.setSerial(perm.getSerial());
            try {
                permissionService.save(permission);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        criarPermissoesAdm();
    }

    private void criarPermissoesAdm() {

        List<SearchCriteriaDTO> criteira = new ArrayList<>();
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setKey("administrador");
        dto.setValue(true);
        dto.setOperation(SearchOperation.EQUAL.name());
        criteira.add(dto);

        BodyListDTO allUsuarios = userService.getAll(criteira, 1);

        PermissionGroupDTO permissionGroupDTO = criarOurecuperarGroup();
        PermissionTypeDTO permissionTypeDTO = criarOuRecuperarType(permissionGroupDTO);
        vincularTypeComPermissoes(permissionTypeDTO);

        if (!allUsuarios.getLista().isEmpty()) {
            for (Object o : allUsuarios.getLista()) {
                UserDTO userDTO = (UserDTO) o;
                vincularUserComType(permissionTypeDTO, userDTO);
            }
        }
    }

    private void vincularUserComType(PermissionTypeDTO permissionTypeDTO, UserDTO userDTO) {

        List<SearchCriteriaDTO> criteira = new ArrayList<>();
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setKey("email");
        dto.setValue(userDTO.getEmail());
        dto.setOperation(SearchOperation.EQUAL.name());
        dto.setClasses("user");
        criteira.add(dto);

        dto = new SearchCriteriaDTO();
        dto.setKey("id");
        dto.setValue(permissionTypeDTO.getId());
        dto.setOperation(SearchOperation.EQUAL.name());
        dto.setClasses("permissionType");
        criteira.add(dto);

        BodyListDTO userAll = userPermissionTypeService.getAll(criteira, 1);

        if (userAll.getLista().isEmpty()) {
            UserPermissionTypeDTO user = new UserPermissionTypeDTO();
            user.setUserEmail(userDTO.getEmail());
            user.setUserName(userDTO.getName());

            user.setPermissionTypeId(permissionTypeDTO.getId());
            user.setPermissionTypeDescricao(permissionTypeDTO.getDescricao());

            userPermissionTypeService.save(user);
        }

    }

    private void vincularTypeComPermissoes(PermissionTypeDTO permissionTypeDTO) {
        List<Permission> all = permissionService.findAllInterno();

        for (Permission permission : all) {

            List<SearchCriteriaDTO> criteira = new ArrayList<>();
            SearchCriteriaDTO dto = new SearchCriteriaDTO();
            dto.setKey("permissionDescription");
            dto.setValue(permission.getPermissionDescription());
            dto.setOperation(SearchOperation.EQUAL.name());
            dto.setClasses("permission");
            criteira.add(dto);

            dto = new SearchCriteriaDTO();
            dto.setKey("descricao");
            dto.setValue(permissionTypeDTO.getDescricao());
            dto.setOperation(SearchOperation.EQUAL.name());
            dto.setClasses("permissionType");
            criteira.add(dto);

            BodyListDTO perfilAll = perfilPermissionTypeService.getAll(criteira, 1);

            if (perfilAll.getLista().isEmpty()) {
                PerfilPermissionTypeDTO typeDTO = new PerfilPermissionTypeDTO();
                typeDTO.setPermissionTypeId(permissionTypeDTO.getId());
                typeDTO.setPermissionTypeDescricao(permissionTypeDTO.getDescricao());

                typeDTO.setPermissionId(permission.getId());
                typeDTO.setPermissionPermissionDescription(permission.getPermissionDescription());

                perfilPermissionTypeService.save(typeDTO);
            }
        }
    }

    private PermissionGroupDTO criarOurecuperarGroup() {

        List<SearchCriteriaDTO> criteira = new ArrayList<>();
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setKey("descricao");
        dto.setValue("ADMINISTRADOR");
        dto.setOperation(SearchOperation.EQUAL.name());
        criteira.add(dto);

        BodyListDTO allGrupos = permissionGroupService.getAll(criteira, 1);

        if (allGrupos.getLista().isEmpty()) {
            PermissionGroupDTO permissionGroup = new PermissionGroupDTO();
            permissionGroup.setDescricao("ADMINISTRADOR");
            permissionGroupService.save(permissionGroup);

            allGrupos = permissionGroupService.getAll(criteira, 1);
        }


        return (PermissionGroupDTO) allGrupos.getLista().get(0);
    }

    private PermissionTypeDTO criarOuRecuperarType(PermissionGroupDTO groupDTO) {

        List<SearchCriteriaDTO> criteira = new ArrayList<>();
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setKey("descricao");
        dto.setValue("GERAL");
        dto.setOperation(SearchOperation.EQUAL.name());
        criteira.add(dto);

        dto = new SearchCriteriaDTO();
        dto.setKey("descricao");
        dto.setValue(groupDTO.getDescricao());
        dto.setOperation(SearchOperation.EQUAL.name());
        dto.setClasses("permissionGroup");
        criteira.add(dto);

        BodyListDTO allTypes = permissionTypeService.getAll(criteira, 1);

        if (allTypes.getLista().isEmpty()) {
            PermissionTypeDTO permissionType = new PermissionTypeDTO();
            permissionType.setDescricao("GERAL");
            permissionType.setPermissionGroupDescricao(groupDTO.getDescricao());
            permissionType.setPermissionGroupId(groupDTO.getId());
            permissionTypeService.save(permissionType);

            allTypes = permissionTypeService.getAll(criteira, 1);
        }



        return (PermissionTypeDTO) allTypes.getLista().get(0);
    }
}