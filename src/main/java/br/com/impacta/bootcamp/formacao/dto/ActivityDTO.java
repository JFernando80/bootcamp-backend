package br.com.impacta.bootcamp.formacao.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import java.util.UUID;
import lombok.Data;

@Data
public class ActivityDTO {

    private UUID id;

    @Validation(required = true, lengthMax = 100, lengthMin = 5)
    private String type;

    @Validation(required = true, lengthMin = 2, lengthMax = 150)
    private String configJson;

    @Validation(required = true, lengthMax = 100, lengthMin = 1)
    private Long maxScore;

    @Validation(required = true, lengthMax = 100, lengthMin = 1)
    private Long passingScore;

    private String createdAtS;

    private String updatedAtS;

    @ClassePersonal (exibe = "description")
    private UUID moduleId;

    @Validation(required = true, lengthMax = 300, lengthMin = 10)
    private String moduleDescription;


}
