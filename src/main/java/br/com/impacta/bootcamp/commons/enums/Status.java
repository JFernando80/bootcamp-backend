package br.com.impacta.bootcamp.commons.enums;

import br.com.impacta.bootcamp.commons.util.EnumUtils;

public enum Status implements EnumUtils {

    ATIVO("ativo"), INATIVO("inativo");

    public String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

}
