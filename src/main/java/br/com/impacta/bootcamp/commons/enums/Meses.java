package br.com.impacta.bootcamp.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Meses {
    JANEIRO(1, 31),
    FEVEREIRO(2, 28),
    MARCO(3, 31),
    ABRIL(4, 30),
    MAIO(5, 31),
    JUNHO(6, 30),
    JULHO(7, 31),
    AGOSTO(8, 31),
    SETEMBRO(9, 30),
    OUTUBRO(10, 31),
    NOVEMBRO(11,30),
    DEZEMBRO(12, 31),
    ;

    private int mes;
    private int dias;

    public static Meses getByMes(int mes) {
        return Stream.of(Meses.values())
                .filter(m -> m.mes == mes)
                .findFirst()
                .orElse(null);
    }
}
