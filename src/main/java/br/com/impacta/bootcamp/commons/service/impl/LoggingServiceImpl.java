package br.com.impacta.bootcamp.commons.service.impl;

import br.com.impacta.bootcamp.commons.service.LoggingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class LoggingServiceImpl implements LoggingService {

    @Override
    public void displayReq(HttpServletRequest request, Object body) {
        StringBuilder reqMessage = new StringBuilder();
        Map<String,String> parameters = getParameters(request);

        reqMessage.append("REQUEST ");
        reqMessage.append("method = [").append(request.getMethod()).append("]");
        reqMessage.append(" path = [").append(request.getRequestURI()).append("] ");

        if(!parameters.isEmpty()) {
            reqMessage.append(" parameters = [").append(parameters).append("] ");
        }

        if(!Objects.isNull(body)) {
            reqMessage.append(" body = [").append(body).append("]");
        }

        log.info("log Request: {}", reqMessage);
    }

    @Override
    public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
        StringBuilder respMessage = new StringBuilder();
        Map<String,String> headers = getHeaders(response);
        respMessage.append("RESPONSE ");
        respMessage.append(" method = [").append(request.getMethod()).append("]");
        if(!headers.isEmpty()) {
            respMessage.append(" ResponseHeaders = [").append(headers).append("]");
        }


        if (!contains(request)) {
            try {
                respMessage.append(" responseBody = [").append(new ObjectMapper().writeValueAsString(body)).append("]");
            } catch (JsonProcessingException ignored) {

            }
        }



        log.info("logResponse: {}",respMessage);
    }

    private boolean contains(HttpServletRequest request) {
        List<String> lista = new ArrayList<>();
        lista.add("atendido_documentos");
        lista.add("responsavel_documentos");
        lista.add("pessoa_documento");
        lista.add("login");
        lista.add("token");
        lista.add("pessoa_composicao_familiar_documento/all");


        for (String s : lista) {
            if (request.getRequestURI().contains(s)) {
                return true;
            }
        }
        return false;
    }

    private Map<String,String> getHeaders(HttpServletResponse response) {
        Map<String,String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for(String str : headerMap) {
            headers.put(str,response.getHeader(str));
        }
        return headers;
    }

    private Map<String,String> getParameters(HttpServletRequest request) {
        Map<String,String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName,paramValue);
        }
        return parameters;
    }
}
