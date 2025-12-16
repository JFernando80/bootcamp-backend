package br.com.impacta.bootcamp.admin.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClassesDTO {

    private long id;

    private String name;

    @Validation(required = true, lengthMax = 100, lengthMin = 3)
    private String simpleName;

}
