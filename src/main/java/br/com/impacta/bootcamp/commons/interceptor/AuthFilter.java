package br.com.impacta.bootcamp.commons.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            // Lógica que pode lançar exceção
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // O resolver manda a exceção para o seu @ControllerAdvice
            resolver.resolveException(request, response, null, e);
        }
    }
}
