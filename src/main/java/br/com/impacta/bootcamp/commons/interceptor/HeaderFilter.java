package br.com.impacta.bootcamp.commons.interceptor;


import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.service.TokenService;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.model.Content;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Order(0)
@Slf4j
public class HeaderFilter implements Filter {

    @Autowired
    private TokenService tokenService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException, BusinessRuleException {

        HttpServletRequest req = (HttpServletRequest) request;

        String correlationId = req.getHeader(CORRELATION_ID_HEADER_NAME);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(CORRELATION_ID_HEADER_NAME, correlationId);

        String value = req.getHeader("authorization");
        String usuario = "";
        if (Objects.nonNull(value)) {
            request.setAttribute("content", buscarToken(value));
        }

        String body = "";
        if ("POST".equalsIgnoreCase(((HttpServletRequest) request).getMethod()) ||
                "PUT".equalsIgnoreCase(((HttpServletRequest) request).getMethod())) {

            if (request.getContentType() != null && !request.getContentType().contains("multipart")) {
                request = new RequestWrapper((HttpServletRequest) request);
                body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }

        if (! ((HttpServletRequest) request).getMethod().equalsIgnoreCase("options") ) {
            log.info(((HttpServletRequest) request).getRequestURI()+" - " +((HttpServletRequest) request).getMethod() + " " + body + " - " + usuario + " - "+value+ " - "+request.getRemoteAddr());
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MDC.remove(CORRELATION_ID_HEADER_NAME);
        }
    }

    @Override
    public void destroy() {

    }

    private Content buscarToken(String value) {

        try {
            TokenDTO tokenDTO = new TokenDTO();
            tokenDTO.setToken(value);
            return tokenService.montarContentFromToken(tokenDTO);

        } catch (Exception e) {
            log.error("erro ao buscar o token de acesso - " + value) ;
            throw new BusinessRuleException("Erro ao buscar o token");
        }
    }
}
