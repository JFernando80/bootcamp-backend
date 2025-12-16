package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.LoginDTO;
import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.dto.UsuarioLogadoDTO;
import br.com.impacta.bootcamp.admin.model.User;

public interface LoginService {

    UsuarioLogadoDTO montarUsuarioLogadoDTO(User user, boolean login);

    String getToken(String token, LoginDTO loginDTO);

    UsuarioLogadoDTO getPorToken(TokenDTO token);

    UsuarioLogadoDTO getPorRefreshToken(TokenDTO token, String securityId);
}
