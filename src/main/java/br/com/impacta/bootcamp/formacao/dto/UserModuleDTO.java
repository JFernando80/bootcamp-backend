package br.com.impacta.bootcamp.formacao.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import java.util.UUID;
import lombok.Data;

@Data
public class UserModuleDTO {

    private UUID id;

    @Validation(dateMax = "+0d")
    private String startedAtS;

    @Validation(dateMax = "+0d")
    private String completedAtS;

    private String status;

    private Long score;

    @ClassePersonal (exibe = "name")
    private UUID userId;

    private String userName;


    @ClassePersonal (exibe = "title")
    private UUID moduleId;

    @Validation(required = true, lengthMax = 100, lengthMin = 10)
    private String moduleTitle;


}
