package br.com.impacta.bootcamp.admin.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.Data;

@Data
public class TokenDTO {

    private long id;

    @Validation(required = true, lengthMax = 150, lengthMin = 20)
    private String token;

    @Validation(required = true,  lengthMin = 100)
    private Long expiraEm;

    @Validation(required = true, lengthMax = 150, lengthMin = 20)
    private String refreshToken;

}
