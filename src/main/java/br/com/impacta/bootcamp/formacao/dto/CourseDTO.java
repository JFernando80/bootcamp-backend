package br.com.impacta.bootcamp.formacao.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import java.util.UUID;
import lombok.Data;

@Data
public class CourseDTO {

    private UUID id;

    private String slug;

    @Validation(required = true, lengthMax = 100, lengthMin = 10)
    private String title;

    @Validation(required = true, lengthMax = 300, lengthMin = 10)
    private String description;



    private String publishedAtS;

    private String createdAtS;

    private String updatedAtS;

}
