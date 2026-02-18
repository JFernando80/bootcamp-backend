package br.com.impacta.bootcamp.formacao.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CertificateDTO {

    private UUID id;

    private UUID token;

    private String userName;

    private String data;
}
