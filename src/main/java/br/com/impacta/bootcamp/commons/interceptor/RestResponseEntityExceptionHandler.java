package br.com.impacta.bootcamp.commons.interceptor;

import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.exception.TokenException;
import br.com.impacta.bootcamp.commons.model.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    protected ResponseEntity<Object> handleConflict(
            BusinessRuleException ex, WebRequest request) {

        JsonResponse jsonResponse = new JsonResponse();
        if (ex.erros.isEmpty()) {
            jsonResponse.setMessage(ex.getMessage());

        } else if (ex.personalizado) {
            jsonResponse.setMessage(ex.getMessage());
            if (!ex.erros.isEmpty()) {
                jsonResponse.setBody(ex.erros.get(0));
            }
        } else {
            jsonResponse.setMessage("Campos inválidos");
            while(ex.erros.size() > 3) {
                ex.erros.remove(3);
            }

            jsonResponse.setBody(ex.erros);
        }
        jsonResponse.setStatusCode(404);

        return new ResponseEntity<>(jsonResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenException.class)
    protected ResponseEntity<Object> erroToken(
            TokenException ex, WebRequest request) {

        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setMessage(ex.getMessage());
        jsonResponse.setStatusCode(401);

        return new ResponseEntity<>(jsonResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleError(
            Exception ex, WebRequest request) {

        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setMessage(ex.getMessage());
        jsonResponse.setStatusCode(500);

        return new ResponseEntity<>(jsonResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
