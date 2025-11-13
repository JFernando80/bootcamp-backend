package br.com.impacta.bootcamp.admin;

import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.model.Token;
import br.com.impacta.bootcamp.admin.repository.TokenRepository;
import br.com.impacta.bootcamp.admin.service.impl.TokenServiceImpl;
import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.util.UtilTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TokenServiceTest {

    @InjectMocks
    private TokenServiceImpl service;

    @Mock
    private TokenRepository tokenRepository;

    private Beans beans = new Beans();
    private static final Long id = 123L;
    private static final Long idErro = 2L;

    private TokenDTO montarNull() {
        return new TokenDTO();
    }

    private TokenDTO montarDtoOK() {
        TokenDTO dtoOK = new TokenDTO();

        dtoOK.setId(id);
        dtoOK.setId(123L);
        dtoOK.setToken("aaaaaaaaaaaaaaaaaaaa");
        dtoOK.setExpiraEm(100L);
        dtoOK.setRefreshToken("aaaaaaaaaaaaaaaaaaaa");
        return dtoOK;
    }

    private TokenDTO montarDtoOkLimite() {
        TokenDTO dtoLimite = new TokenDTO();

        dtoLimite.setToken("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setExpiraEm(100L);
        dtoLimite.setRefreshToken("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private TokenDTO montarDtoErroMin() {
        TokenDTO dtoErroMin = new TokenDTO();

        dtoErroMin.setToken("aaaaaaaaaaaaaaaaaaa");
        dtoErroMin.setExpiraEm(99L);
        dtoErroMin.setRefreshToken("aaaaaaaaaaaaaaaaaaa");
        return dtoErroMin;
    }

    private TokenDTO montarErroMax() {
        TokenDTO dtoErroMax = new TokenDTO();

        dtoErroMax.setToken("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setExpiraEm(100L);
        dtoErroMax.setRefreshToken("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoErroMax;
    }

    @BeforeEach
    public  void setup() {
        ReflectionTestUtils.setField(service, "beans", new Beans());
        ReflectionTestUtils.setField(service, "offset", 10);
    }

    @Test
    public void tentarSalvarErroCamposNull() {
        Beans beans = new Beans();
        TokenDTO dtoNull = montarNull();
        try {
            service.save(dtoNull);
        } catch (BusinessRuleException e) {
            Field[] fields = dtoNull.getClass().getDeclaredFields();
            List<Field> obrigatorios = new ArrayList<>();
            for (Field field : fields) {
                String campo = Util.toSneakCase(field.getName());
                String hash = beans.toHexString(campo);
                for (String erro : e.erros) {

                    if (erro.contains(hash)) {
                        if (Util.isTipoDTO(field)) {
                            Assertions.assertEquals("A classe "+campo+" nao pode ser nula. "+hash, erro);
                        } else {
                            Assertions.assertEquals("O campo "+campo+" é obrigatório. "+hash, erro);
                        }
                    }
                }

                if (UtilTest.olharAnotacaoRequired(field)) {
                    obrigatorios.add(field);
                }
            }

            Assertions.assertEquals(e.erros.size(), obrigatorios.size());
        }
    }

    @Test
    public void tentarSalvarErroCamposMinimo() {
        Beans beans = new Beans();
        TokenDTO erroMin = montarDtoErroMin();
        try {
            service.save(erroMin);
        } catch (BusinessRuleException e) {
            Field[] fields = erroMin.getClass().getDeclaredFields();
            List<Field> obrigatorios = new ArrayList<>();
            for (Field field : fields) {
                String campo = Util.toSneakCase(field.getName());
                String hash = beans.toHexString(campo);
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                for (String erro : e.erros) {
                    if (erro.contains(hash)) {
                        Long minValue = UtilTest.olharAnotacaoLengthMin(field);
                        if (field.getType().equals(Long.class)) {
                            Assertions.assertEquals("O valor do campo "+campo+" deve ser maior que "+minValue+". "+hash, erro);
                        } else if (valid  != null && !valid.dateMin().equalsIgnoreCase("")) {
                            Assertions.assertEquals("O campo "+campo+" é menor que o permitido. Data Minima maior que "+beans.converterDateToString(beans.validarDateMin(valid.dateMin(), 0))+". "+hash, erro);
                        } else if (field.getType().equals(String.class)) {
                            Assertions.assertEquals("O campo "+campo+" deve ter mais que "+minValue+" caracter/es. "+hash, erro);
                        }
                    }
                }

                if (UtilTest.olharAnotacaoMin(field)) {
                    obrigatorios.add(field);
                }
            }

            Assertions.assertEquals(e.erros.size(), obrigatorios.size());
        }
    }

    @Test
    public void tentarSalvarErroCamposMaximo() {
        Beans beans = new Beans();
        TokenDTO erroMax = montarErroMax();
        try {
            service.save(erroMax);
        } catch (BusinessRuleException e) {
            Field[] fields = erroMax.getClass().getDeclaredFields();
            List<Field> obrigatorios = new ArrayList<>();
            for (Field field : fields) {
                String campo = Util.toSneakCase(field.getName());
                String hash = beans.toHexString(campo);
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                for (String erro : e.erros) {
                    if (erro.contains(hash)) {
                        Long maxValue = UtilTest.olharAnotacaoLengthMax(field);
                        if (field.getType().equals(Long.class)) {
                            Assertions.assertEquals("O valor do campo "+campo+" deve ser menor que "+maxValue+". "+hash, erro);
                        } else if (valid  != null && !valid.dateMax().equalsIgnoreCase("")) {
                            Assertions.assertEquals("O campo "+campo+" é maior que o permitido. Data Máxima: "+beans.converterDateToString(beans.validarDateMax(valid.dateMax(), 0))+". "+hash, erro);
                        } else if (field.getType().equals(String.class)) {
                            Assertions.assertEquals("O campo "+campo+" deve ter menos que "+maxValue+" caracter/es. "+hash, erro);
                        }
                    }
                }

                if (UtilTest.olharAnotacaoMax(field)) {
                    obrigatorios.add(field);
                }
            }

            Assertions.assertEquals(e.erros.size(), obrigatorios.size());
        }
    }

    @Test
    public void tentarSalvarErroRepetido() {
        TokenDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Token token = new Token();
        beans.updateObjectos(token, dtoOk);

        token.setExpiraEm(beans.converterLongToDate(dtoOk.getExpiraEm()));
        token.setId(idErro);

        List<Token> tokens = new ArrayList<>();
        tokens.add(token);
        Page<Token> msPage = new PageImpl<>(tokens);

        Mockito.when(tokenRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este token cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        TokenDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Token token = new Token();
        beans.updateObjectos(token, dtoOk);
        token.setExpiraEm(beans.converterLongToDate(dtoOk.getExpiraEm()));

        List<Token> tokens = new ArrayList<>();
        tokens.add(token);
        Page<Token> msPage = new PageImpl<>(tokens);
        Mockito.when(tokenRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(tokenRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        TokenDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Token token = new Token();
        beans.updateObjectos(token, dtoOk);
        token.setExpiraEm(beans.converterLongToDate(dtoOk.getExpiraEm()));

        List<Token> tokens = new ArrayList<>();
        tokens.add(token);

        Page<Token> msPage = new PageImpl<>(tokens);
        Mockito.when(tokenRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(tokenRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        TokenDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Token token = new Token();
        beans.updateObjectos(token, dtoOk);
        Mockito.when(tokenRepository.findById(id)).thenReturn(Optional.of(token));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Token não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        TokenDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Token token = new Token();
        beans.updateObjectos(token, dtoOk);
        Mockito.when(tokenRepository.findById(id)).thenReturn(Optional.of(token));
        Assertions.assertEquals(token, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        TokenDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Token token = new Token();
        beans.updateObjectos(token, dtoOk);
        token.setExpiraEm(beans.converterLongToDate(dtoOk.getExpiraEm()));

        List<Token> tokens = new ArrayList<>();
        tokens.add(token);
        Page<Token> msPage = new PageImpl<>(tokens);

        Mockito.when(tokenRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(tokenRepository.findById(id)).thenReturn(Optional.of(token));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(tokenRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        TokenDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Token token = new Token();
        beans.updateObjectos(token, dtoOk);
        token.setExpiraEm(beans.converterLongToDate(dtoOk.getExpiraEm()));

        List<Token> tokens = new ArrayList<>();
        tokens.add(token);
        Page<Token> msPage = new PageImpl<>(tokens);

        Mockito.when(tokenRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(tokenRepository.findById(id)).thenReturn(Optional.of(token));
        Mockito.when(tokenRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        TokenDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Token token = new Token();
        beans.updateObjectos(token, dtoOk);

        Mockito.when(tokenRepository.findById(id)).thenReturn(Optional.of(token));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(tokenRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        TokenDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Token token = new Token();
        beans.updateObjectos(token, dtoOk);
        token.setExpiraEm(beans.converterLongToDate(dtoOk.getExpiraEm()));

        TokenDTO n = new TokenDTO();
        beans.updateObjectos(n, token);
        n.setExpiraEm(dtoOk.getExpiraEm());

        TokenDTO novo = service.montarDTO(token);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
