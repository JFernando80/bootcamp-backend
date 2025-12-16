package br.com.impacta.bootcamp.commons.enums;

import java.util.Objects;

public enum SearchOperation {

    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_EQUAL,
    LESS_THAN_EQUAL,
    NOT_EQUAL,
    EQUAL,
    MATCH,
    MATCH_START,
    MATCH_END,
    IN,
    NOT_IN,
    IS_NULL,
    NOT_NULL,
    BETWEEN,

    ;


    public static SearchOperation find(String valor) {
        for (int i = 0; i < SearchOperation.values().length; i++) {
            if (Objects.nonNull(valor) && SearchOperation.values()[i].name().equalsIgnoreCase(valor)) {
                return SearchOperation.values()[i];
            }
        }
        return null;
    }
}
