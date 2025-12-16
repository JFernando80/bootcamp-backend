package br.com.impacta.bootcamp.admin.dto;

import java.util.UUID;

import br.com.impacta.bootcamp.commons.util.Unico;
import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.Data;

@Data
public class UserDTO {

    private UUID id;

    @Validation(required = true, lengthMax = 120, lengthMin = 2)
    private String name;

    @Validation(required = true, lengthMax = 120, lengthMin = 10)
    private String email;

    @Validation(required = true, lengthMax = 120, lengthMin = 3)
    private String sobrenome;

    private Boolean administrador;

    @Unico
    @Validation(required = true, lengthMax = 120, lengthMin = 35)
    private String passwordHash;

    private String createdAtS;

    private String updatedAtS;

    private String deletedAtS;

    private String salt;

}
