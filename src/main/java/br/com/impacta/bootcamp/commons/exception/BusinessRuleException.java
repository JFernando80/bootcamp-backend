package br.com.impacta.bootcamp.commons.exception;

import java.util.ArrayList;
import java.util.List;

public class BusinessRuleException extends RuntimeException  {

    public List<String> erros = new ArrayList<>();

    public boolean personalizado = false;

    public BusinessRuleException(String message, String ... value) {
        super(java.text.MessageFormat.format(message, value));
    }

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(List<String> erros) {
        this.erros = erros;
    }

    public BusinessRuleException(String message, List<String> erros) {
        super(message);
        this.erros = erros;
        this.personalizado = true;
    }
}
