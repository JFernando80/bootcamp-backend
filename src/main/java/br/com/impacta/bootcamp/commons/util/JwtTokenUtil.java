package br.com.impacta.bootcamp.commons.util;

import br.com.impacta.bootcamp.admin.dto.UserDTO;
import br.com.impacta.bootcamp.admin.dto.UsuarioLogadoDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    // Chave secreta para assinar o token. É lida do application.properties.
    @Value("${jwt.secret}")
    private String secret;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    public String generateToken(UsuarioLogadoDTO userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissoes", userDetails.getPermissionsDTOS());
        claims.put("token", userDetails.getTokenDTO());

        return createToken(claims, userDetails.getUserDTO().getName());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Define a validade do token (ex: 10 horas)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSigningKey())
                .compact();
    }
}
