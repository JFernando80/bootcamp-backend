package br.com.impacta.bootcamp.commons.enums;

public enum TypesClasses {

    STRING,
    LONG,
    INTEGER,
    DOUBLE,
    INT,
    LIST,
    ENUM,
    DATE,
    BOOLEAN;

    public static boolean existe(String valor) {
        for (int i = 0; i < TypesClasses.values().length; i++) {
            if (valor != null && TypesClasses.values()[i].name().equals(valor.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
