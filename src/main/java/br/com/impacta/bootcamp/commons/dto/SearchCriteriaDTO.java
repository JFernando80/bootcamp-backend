package br.com.impacta.bootcamp.commons.dto;

import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDTO {

    private String key;
    private Object value;
    private String operation;
    private String classes;
    private Object value2;

    public static SearchCriteriaDTO montarEqual(String value, String key) {
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setKey(key);
        dto.setValue(value);
        dto.setOperation(SearchOperation.EQUAL.name());
        return dto;
    }

    public static SearchCriteriaDTO montarIsNull(String key) {
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setKey(key);
        dto.setOperation(SearchOperation.IS_NULL.name());
        return dto;
    }

    public static SearchCriteriaDTO montarEqual(Object value, String key, String classe) {
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setKey(key);
        dto.setValue(value);
        dto.setOperation(SearchOperation.EQUAL.name());
        dto.setClasses(classe);
        return dto;
    }

    public static SearchCriteriaDTO montarSearchCriteriaDTO(SearchCriteriaDTO dto) {
        SearchCriteriaDTO criteriaDTO = new SearchCriteriaDTO();
        criteriaDTO.setOperation(dto.getOperation());
        criteriaDTO.setKey(dto.getKey());
        criteriaDTO.setClasses(dto.getClasses());
        criteriaDTO.setValue(dto.getValue());
        criteriaDTO.setValue2(dto.getValue2());

        return criteriaDTO;
    }
}
