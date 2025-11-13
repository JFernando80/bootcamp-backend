package br.com.impacta.bootcamp.seguranca.dto;

import lombok.Data;

@Data
public class SecurityDTO {

    private long id;
    private String publicKey;
    private String screen;
    private Long userId;
}
