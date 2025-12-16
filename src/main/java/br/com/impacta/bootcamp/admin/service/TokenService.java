package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.model.Token;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.model.Content;

import java.util.List;

public interface TokenService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(TokenDTO dto);

    void update(TokenDTO dto);

    Token findByIdInterno(Long id);

    void delete(Long id);

    TokenDTO montarDTO(Token entity);

    TokenDTO getTokenByUser(User user, boolean login);

    Token getTokenByToken(String token);

    Token getRefreshTokenByUser(String refreshToken, long l);

    Content montarContentFromToken(TokenDTO tokenDTO);
}
