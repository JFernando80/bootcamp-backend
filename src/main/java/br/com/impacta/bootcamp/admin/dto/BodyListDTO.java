package br.com.impacta.bootcamp.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class BodyListDTO {

    private List<Object> lista;
    private long total;
    private int pagina;
}
