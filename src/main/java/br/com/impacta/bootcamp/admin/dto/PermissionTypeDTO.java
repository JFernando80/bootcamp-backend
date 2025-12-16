package br.com.impacta.bootcamp.admin.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.Data;

@Data
public class PermissionTypeDTO {

    private long id;

    @Validation(required = true, lengthMax = 150, lengthMin = 3)
    private String descricao;

    private String permissionGroupDescricao;

    private Long permissionGroupId;

}
