package br.com.impacta.bootcamp.admin;
import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.admin.dto.UserDTO;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.repository.UserRepository;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.admin.service.impl.UserServiceImpl;
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
import java.util.*;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository userRepository;

    private Beans beans = new Beans();
    private static final UUID id = UUID.randomUUID();
    private static final UUID idErro = UUID.randomUUID();

    private UserDTO montarNull() {
        return new UserDTO();
    }

    private UserDTO montarDtoOK() {
        UserDTO dtoOK = new UserDTO();

        dtoOK.setId(id);
        dtoOK.setName("aaaaaaaaaa");
        dtoOK.setEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoOK.setSobrenome("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoOK.setPasswordHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoOK.setCreatedAtS(beans.getDataPattern("+1d", "max", -2));
        dtoOK.setUpdatedAtS(beans.getDataPattern("+1d", "max", -2));
        dtoOK.setDeletedAtS(beans.getDataPattern("+1d", "max", -2));
        return dtoOK;
    }

    private UserDTO montarDtoOkLimite() {
        UserDTO dtoLimite = new UserDTO();

        dtoLimite.setName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setSobrenome("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setPasswordHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private UserDTO montarDtoErroMin() {
        UserDTO dtoErroMin = new UserDTO();

        dtoErroMin.setName("a");
        dtoErroMin.setEmail("aaaaaaaaa");
        dtoErroMin.setSobrenome("aa");
        dtoErroMin.setPasswordHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoErroMin;
    }

    private UserDTO montarErroMax() {
        UserDTO dtoErroMax = new UserDTO();

        dtoErroMax.setName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setSobrenome("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setPasswordHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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
        UserDTO dtoNull = montarNull();
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
        UserDTO erroMin = montarDtoErroMin();
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
        UserDTO erroMax = montarErroMax();
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
        UserDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        User user = new User();
        beans.updateObjectos(user, dtoOk);

        user.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        user.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));
        user.setDeletedAt(beans.converterStringToDate(dtoOk.getDeletedAtS()));
        user.setId(idErro);

        List<User> users = new ArrayList<>();
        users.add(user);
        Page<User> msPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este user cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        UserDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        User user = new User();
        beans.updateObjectos(user, dtoOk);
        List<User> users = new ArrayList<>();
        users.add(user);
        Page<User> msPage = new PageImpl<>(users);
        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(userRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        UserDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        User user = new User();
        beans.updateObjectos(user, dtoOk);

        List<User> users = new ArrayList<>();
        users.add(user);

        Page<User> msPage = new PageImpl<>(users);
        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(userRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        UserDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        User user = new User();
        beans.updateObjectos(user, dtoOk);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("User não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        UserDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        User user = new User();
        beans.updateObjectos(user, dtoOk);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Assertions.assertEquals(user, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        UserDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        User user = new User();
        beans.updateObjectos(user, dtoOk);

        List<User> users = new ArrayList<>();
        users.add(user);
        Page<User> msPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(userRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        UserDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        User user = new User();
        beans.updateObjectos(user, dtoOk);

        List<User> users = new ArrayList<>();
        users.add(user);
        Page<User> msPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        UserDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        User user = new User();
        beans.updateObjectos(user, dtoOk);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(userRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        UserDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        User user = new User();
        beans.updateObjectos(user, dtoOk);
        user.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        user.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));
        user.setDeletedAt(beans.converterStringToDate(dtoOk.getDeletedAtS()));

        UserDTO n = new UserDTO();
        beans.updateObjectos(n, user);
        n.setCreatedAtS(dtoOk.getCreatedAtS());
        n.setUpdatedAtS(dtoOk.getUpdatedAtS());
        n.setDeletedAtS(dtoOk.getDeletedAtS());

        UserDTO novo = service.montarDTO(user);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
