package br.com.impacta.bootcamp.admin.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserPermissionTypeDTO {

    private Long id;

    private String userName;

    private String userEmail;

    private String permissionTypeDescricao;

    private Long permissionTypeId;

}
