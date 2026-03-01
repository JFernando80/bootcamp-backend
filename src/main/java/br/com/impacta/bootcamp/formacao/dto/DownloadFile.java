package br.com.impacta.bootcamp.formacao.dto;

import lombok.Data;

@Data
public class DownloadFile {

    private String tipo;
    private byte[] arquivo;
    private String filePath;
}
