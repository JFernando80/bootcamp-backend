package br.com.impacta.bootcamp.commons.interceptor;


import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.dto.UserDTO;
import br.com.impacta.bootcamp.admin.dto.UsuarioLogadoDTO;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.commons.dto.PermissionsDTO;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.model.Content;
import br.com.impacta.bootcamp.commons.model.JsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(0)
@Slf4j
public class HeaderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.autorizador}")
    private String urlServer;

    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException, BusinessRuleException {

        HttpServletRequest req = (HttpServletRequest) request;

        String correlationId = req.getHeader(CORRELATION_ID_HEADER_NAME);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(CORRELATION_ID_HEADER_NAME, correlationId);

        String url = urlServer+"autorizar";
        String value = req.getHeader("autorization");
        String companyCNPJ = req.getHeader("company");
        String usuario = "";
        if (Objects.nonNull(value)) {
            ObjectMapper mapper = new ObjectMapper();

            ResponseEntity<JsonResponse> responseEntity = buscarToken(value, companyCNPJ, url);

            if (Objects.nonNull(responseEntity.getBody()) ) {
                Map<String, Object> map = (Map<String, Object>) responseEntity.getBody().getBody();
                User user = mapper.convertValue(map.get("user"), User.class);

                String local = map.get("locale").toString();
                Locale locale = new Locale(local.substring(0, local.indexOf("_")), local.substring(local.indexOf("_")+1));

                Map<String, Object> logado = (Map<String, Object>) map.get("usuarioLogadoDTO");
                UsuarioLogadoDTO logadoDTO = new UsuarioLogadoDTO();
                logadoDTO.setUserDTO(mapper.convertValue(logado.get("userDTO"), UserDTO.class));
                if (logado.containsKey("logo") && logado.get("logo") != null) {
                    logadoDTO.setLogo(logado.get("logo").toString());
                }
                logadoDTO.setTokenDTO(mapper.convertValue(logado.get("tokenDTO"), TokenDTO.class));
                List lista = (ArrayList) logado.get("permissionsDTOS");
                for (Object o : lista) {
                    Map<String, Object> permissionT = (Map<String, Object>) o;
                    Long permission = Long.parseLong(permissionT.get("permission").toString());
                    logadoDTO.getPermissionsDTOS().add(new PermissionsDTO(permission));
                }

                Content content = new Content();
                content.setUser(user);
                content.setLocale(locale);
                content.setUsuarioLogadoDTO(logadoDTO);
                usuario = content.getUser().getName();
                request.setAttribute("content", content);
            }
        } else {
            Content content = new Content();
            content.setIp(request.getRemoteAddr());
            request.setAttribute("content", content);
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
        } finally {
            MDC.remove(CORRELATION_ID_HEADER_NAME);
        }
    }

    @Override
    public void destroy() {

    }

    private ResponseEntity<JsonResponse> buscarToken(String value, String companyCNPJ, String url) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            TokenDTO tokenDTO = new TokenDTO();
            tokenDTO.setToken(value);
            HttpEntity<String> requestEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(tokenDTO ),  headers);

            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    JsonResponse.class
            );

        } catch (Exception e) {
            log.error("erro ao buscar o token de acesso - " + value) ;
            throw new BusinessRuleException("Erro ao buscar o token");
        }
    }
}
