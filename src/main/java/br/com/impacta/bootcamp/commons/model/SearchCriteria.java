package br.com.impacta.bootcamp.commons.model;

import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import lombok.Data;
import lombok.Value;

@Data
@Value
public class SearchCriteria {

    private String key;
    private Object value;
    private SearchOperation operation;
    private String classes;
    private Object value2;
    private Boolean or;

}
