package br.com.impacta.bootcamp.admin.dto;

import br.com.impacta.bootcamp.commons.enums.Status;
import br.com.impacta.bootcamp.commons.util.Validation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class ClassesVariaveisDTO {

    private Long id;

    private ClassesDTO classesDTO;

    @Validation(required = true, lengthMax = 70, lengthMin = 10)
    private String variavel;

    @Validation(required = true, lengthMax = 70, lengthMin = 10)
    private String tipo;

    @Validation(required = true)
    private Status status;

    private String header;

    private List<String> ordem;

    public String getTipo() {
        return tipo.toLowerCase();
    }

    public static ClassesVariaveisDTO montar(ClassesVariaveisDTO dto, String valor) {
        dto.setHeader(dto.getHeader()+" "+valor);
        return dto;
    }

}
