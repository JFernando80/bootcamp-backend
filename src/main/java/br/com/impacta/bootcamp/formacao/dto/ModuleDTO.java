package br.com.impacta.bootcamp.formacao.dto;

import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.Data;

import java.util.UUID;

@Data
public class ModuleDTO {

    private UUID id;

    @Validation(required = true, lengthMax = 100, lengthMin = 1)
    private Long index;

    @Validation(required = true, lengthMax = 100, lengthMin = 10)
    private String title;

    @Validation(required = true, lengthMax = 300, lengthMin = 10)
    private String description;

    private Boolean requiredToCompleteCourse;

    @Validation(dateMax = "+0d")
    private String createdAtS;

    @Validation(dateMax = "+0d")
    private String updatedAtS;

    @ClassePersonal (exibe = "description")
    private UUID courseId;

    @Validation(required = true, lengthMax = 300, lengthMin = 10)
    private String courseDescription;

}
