package br.com.impacta.bootcamp.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonResponse {

    private int statusCode;
    private String message;
    private Object body;

    public static JsonResponse ok(Object o) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setStatusCode(200);
        jsonResponse.setBody(o);
        jsonResponse.setMessage("ok");
        return jsonResponse;
    }

    public static JsonResponse ok(Object o, String message) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setStatusCode(200);
        jsonResponse.setBody(o);
        jsonResponse.setMessage(message);
        return jsonResponse;
    }

    public static JsonResponse ok(String o) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setStatusCode(200);
        jsonResponse.setBody(null);
        jsonResponse.setMessage(o);
        return jsonResponse;
    }

    public static JsonResponse cripto(String o) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setStatusCode(200);
        jsonResponse.setBody(o);
        jsonResponse.setMessage("ok");
        return jsonResponse;
    }
}
