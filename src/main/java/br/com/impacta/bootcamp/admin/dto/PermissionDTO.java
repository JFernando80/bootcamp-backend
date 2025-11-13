package br.com.impacta.bootcamp.admin.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.Data;

@Data
public class PermissionDTO {

    private long id;

    @Validation(required = true, lengthMin = 10, lengthMax = 50)
    private String permission;

    @Validation(required = true, lengthMin = 10, lengthMax = 150)
    private String permissionDescription;

    private String screen;

    @Validation(required = true)
    private Long serial;

}
