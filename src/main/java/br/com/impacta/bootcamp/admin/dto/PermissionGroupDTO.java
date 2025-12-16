package br.com.impacta.bootcamp.admin.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.Data;

@Data
public class PermissionGroupDTO {

    private long id;

    @Validation(required = true, lengthMin = 3, lengthMax = 50)
    private String descricao;


}
