package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.dto.UsuarioLogadoDTO;
import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.admin.model.Token;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.model.UserPermissionType;
import br.com.impacta.bootcamp.admin.repository.TokenRepository;
import br.com.impacta.bootcamp.admin.service.LoginService;
import br.com.impacta.bootcamp.admin.service.TokenService;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.admin.specification.TokenSpecification;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.model.Content;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validador;
import br.com.impacta.bootcamp.seguranca.dto.SecurityDTO;
import br.com.impacta.bootcamp.seguranca.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    private static Integer VALIDADE_TOKEN = 30;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        TokenSpecification msCategoria = new TokenSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<Token> msPage = tokenRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(TokenDTO dto) {
        isRepetido(dto);
        tokenRepository.save(montarEntity(dto));
    }

    @Override
    public void update(TokenDTO dto) {
        updateInterno(dto);
    }

    @Override
    public Token findByIdInterno(Long id) {
        return tokenRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("Token não encontrado"));
    }

    @Override
    public void delete(Long id) {
        Token entity = findByIdInterno(id);
        tokenRepository.delete(entity);
    }

    @Override
    public TokenDTO montarDTO(Token entity) {
        TokenDTO dto = new TokenDTO();
        beans.updateObjectos(dto, entity);
        dto.setExpiraEm(beans.dateToLong(entity.getExpiraEm()));

        return dto;
    }

    @Override
    public Token getTokenByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public Token getRefreshTokenByUser(String refreshToken, long securityId) {
        SecurityDTO segurancaDTO = securityService.findById(securityId);
        securityService.delete(segurancaDTO);

        String salt = segurancaDTO.getPublicKey();
        String loginAndRefreshToken = beans.decrypt(salt, refreshToken);
        String login = loginAndRefreshToken.substring(0, loginAndRefreshToken.indexOf("}*{"));
        String rtoken = loginAndRefreshToken.substring(loginAndRefreshToken.indexOf("}*{")+3, loginAndRefreshToken.length());

        User user = userService.findByEmailInterno(login);
        Token token = tokenRepository.findByRefreshToken(rtoken);

        if (token != null && Objects.equals(token.getUser(), user)) {
            renovarToken(token);
            return token;
        }

        throw new BusinessRuleException("Relogar token invalido");

    }

    @Override
    public TokenDTO getTokenByUser(User user, boolean login) {
        Token token = tokenRepository.findTop1ByUser(user);

        if (token == null) {
            token = createToken(user);
        }

        if (login) {
            renovarToken(token);
            log.info("token {} criado para {}", token.getToken(), user.getName());
        }

        if (!validadeDoToken(token)) {
            throw new BusinessRuleException("relogar no sistema");
        }
        return montarTokenDTO(token);
    }

    @Override
    public Content montarContentFromToken(TokenDTO tokenDTO) {
        Token token = getTokenByToken(tokenDTO.getToken());
        Content content = new Content();

        User user = token.getUser();
        UsuarioLogadoDTO usuarioLogadoDTO = loginService.montarUsuarioLogadoDTO(user, false);

        usuarioLogadoDTO.setTokenDTO(montarTokenDTO(token));

        content.setUser(token.getUser());
        content.setLocale(new Locale("pt", "BR"));
        content.setUsuarioLogadoDTO(usuarioLogadoDTO);

        return content;
    }

    private Boolean validadeDoToken(Token token) {
        Date date = new Date();

        if (token.getExpiraEm().before(date)) {
            return false;
        }

        return true;
    }

    private TokenDTO montarTokenDTO(Token token) {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken(token.getToken());
        tokenDTO.setRefreshToken(token.getRefreshToken());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(token.getExpiraEm());
        tokenDTO.setExpiraEm(calendar.getTimeInMillis());
        return tokenDTO;
    }

    private void renovarToken(Token token) {
        token.setToken(beans.toHexString(UUID.randomUUID().toString()));
        token.setRefreshToken(beans.toHexString(UUID.randomUUID().toString()));
        token.setExpiraEm(DateTime.now().plusMinutes(VALIDADE_TOKEN).toDate());
        tokenRepository.save(token);
    }

    private Token createToken(User user) {
        Token token = new Token();

        token.setExpiraEm(DateTime.now().plusMinutes(VALIDADE_TOKEN).toDate());

        token.setUser(user);

        token.setToken(beans.toHexString(UUID.randomUUID().toString()));
        token.setRefreshToken(beans.toHexString(UUID.randomUUID().toString()));

        tokenRepository.save(token);
        return token;
    }

    private Token montarEntity(TokenDTO dto) {
        Token entity = new Token();
        beans.updateObjectos(entity, dto);
        Date expiraEm = beans.longToDate(dto.getExpiraEm());
        entity.setExpiraEm(expiraEm);

        return entity ;
    }

    private void updateInterno(TokenDTO dto) {
        isRepetido(dto);
        Token entity = findByIdInterno(dto.getId());
        Token updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            tokenRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(TokenDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(TokenDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    TokenDTO dto1 = (TokenDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este token cadastrado");
            }
        }
    }
}
