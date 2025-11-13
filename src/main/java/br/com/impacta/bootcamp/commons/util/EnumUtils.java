package br.com.impacta.bootcamp.commons.util;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

public interface EnumUtils {

    static List<RetornoEnum> allStatus(Enum[] o) {
        List<RetornoEnum> lista = new ArrayList<>();
        for (Enum status: o) {
            lista.add(new RetornoEnum(status.name()));
        }

        return lista;
    }

    @Value
    static class RetornoEnum {
        private String name;
    }
}
