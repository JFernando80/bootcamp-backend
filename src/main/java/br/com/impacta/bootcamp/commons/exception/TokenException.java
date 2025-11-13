package br.com.impacta.bootcamp.commons.exception;

public class TokenException extends RuntimeException  {

    public TokenException(String message, String ... value) {
        super(java.text.MessageFormat.format(message, value));
    }

    public TokenException(String message) {
        super(message);
    }

}
