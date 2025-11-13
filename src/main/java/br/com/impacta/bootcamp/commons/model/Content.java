package br.com.impacta.bootcamp.commons.model;

import br.com.impacta.bootcamp.admin.dto.UsuarioLogadoDTO;
import br.com.impacta.bootcamp.admin.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Locale;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {

    private User user;

    private Locale locale;

    private UsuarioLogadoDTO usuarioLogadoDTO;

    private String ip;
}
