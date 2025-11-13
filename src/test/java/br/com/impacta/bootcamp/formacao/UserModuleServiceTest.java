package br.com.impacta.bootcamp.formacao;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.formacao.dto.UserModuleDTO;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.model.UserModule;
import br.com.impacta.bootcamp.formacao.repository.UserModuleRepository;
import br.com.impacta.bootcamp.formacao.service.ModuleService;
import br.com.impacta.bootcamp.formacao.service.impl.UserModuleServiceImpl;
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
public class UserModuleServiceTest {

    @InjectMocks
    private UserModuleServiceImpl service;

    @Mock
    private UserModuleRepository userModuleRepository;

    @Mock
    private ModuleService moduleService;

    @Mock
    private UserService userService;

    private Beans beans = new Beans();
    private static final UUID id = UUID.randomUUID();
    private static final UUID idErro = UUID.randomUUID();

    private UserModuleDTO montarNull() {
        return new UserModuleDTO();
    }

    private UserModuleDTO montarDtoOK() {
        UserModuleDTO dtoOK = new UserModuleDTO();

        dtoOK.setCompletedAtS(beans.getDataPattern("+1d", "max", -2));
        dtoOK.setStartedAtS(beans.getDataPattern("+1d", "max", -2));
        dtoOK.setId(id);
        dtoOK.setStatus("aaa");
        dtoOK.setScore(123L);
        dtoOK.setUserName("aaa");
        dtoOK.setModuleTitle("aaaaaaaaaa");
        return dtoOK;
    }

    private UserModuleDTO montarDtoOkLimite() {
        UserModuleDTO dtoLimite = new UserModuleDTO();

        dtoLimite.setStartedAtS(beans.getDataPattern("", "max", 2));
        dtoLimite.setCompletedAtS(beans.getDataPattern("", "max", 2));
        dtoLimite.setModuleTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private UserModuleDTO montarDtoErroMin() {
        UserModuleDTO dtoErroMin = new UserModuleDTO();

        dtoErroMin.setModuleTitle("aaaaaaaaa");
        return dtoErroMin;
    }

    private UserModuleDTO montarErroMax() {
        UserModuleDTO dtoErroMax = new UserModuleDTO();

        dtoErroMax.setStartedAtS(beans.getDataPattern("+0d", "max", 20));
        dtoErroMax.setCompletedAtS(beans.getDataPattern("+0d", "max", 20));
        dtoErroMax.setModuleTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoErroMax;
    }

    private User montarUser() {

        User user = new User();
        user.setName("aaa");
        return user;
    }

    private Module montarModule() {

        Module module = new Module();
        module.setIndex(1L);
        module.setTitle("aaaaaaaaaa");
        module.setDescription("aaaaaaaaaa");
        return module;
    }

    @BeforeEach
    public  void setup() {
        ReflectionTestUtils.setField(service, "beans", new Beans());
        ReflectionTestUtils.setField(service, "offset", 10);
        Mockito.when(userService.findByIdInterno(Mockito.any())).thenReturn(montarUser());
        Mockito.when(moduleService.findByIdInterno(Mockito.any())).thenReturn(montarModule());
    }

    @Test
    public void tentarSalvarErroCamposNull() {
        Beans beans = new Beans();
        UserModuleDTO dtoNull = montarNull();
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
        UserModuleDTO erroMin = montarDtoErroMin();
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
        UserModuleDTO erroMax = montarErroMax();
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
        UserModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);

        userModule.setStartedAt(beans.converterStringToDate(dtoOk.getStartedAtS()));
        userModule.setCompletedAt(beans.converterStringToDate(dtoOk.getCompletedAtS()));
        userModule.setId(idErro);
        userModule.setUser(montarUser());
        userModule.setModule(montarModule());

        List<UserModule> userModules = new ArrayList<>();
        userModules.add(userModule);
        Page<UserModule> msPage = new PageImpl<>(userModules);

        Mockito.when(userModuleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este userModule cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        UserModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);
        userModule.setUser(montarUser());
        userModule.setModule(montarModule());

        List<UserModule> userModules = new ArrayList<>();
        userModules.add(userModule);
        Page<UserModule> msPage = new PageImpl<>(userModules);
        Mockito.when(userModuleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(userModuleRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        UserModuleDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);

        userModule.setUser(montarUser());
        userModule.setModule(montarModule());

        List<UserModule> userModules = new ArrayList<>();
        userModules.add(userModule);

        Page<UserModule> msPage = new PageImpl<>(userModules);
        Mockito.when(userModuleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(userModuleRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        UserModuleDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);
        Mockito.when(userModuleRepository.findById(id)).thenReturn(Optional.of(userModule));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("UserModule não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        UserModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);
        Mockito.when(userModuleRepository.findById(id)).thenReturn(Optional.of(userModule));
        Assertions.assertEquals(userModule, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        UserModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);
        userModule.setUser(montarUser());
        userModule.setModule(montarModule());

        List<UserModule> userModules = new ArrayList<>();
        userModules.add(userModule);
        Page<UserModule> msPage = new PageImpl<>(userModules);

        Mockito.when(userModuleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(userModuleRepository.findById(id)).thenReturn(Optional.of(userModule));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(userModuleRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        UserModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);
        userModule.setUser(montarUser());
        userModule.setModule(montarModule());

        List<UserModule> userModules = new ArrayList<>();
        userModules.add(userModule);
        Page<UserModule> msPage = new PageImpl<>(userModules);

        Mockito.when(userModuleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(userModuleRepository.findById(id)).thenReturn(Optional.of(userModule));
        Mockito.when(userModuleRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        UserModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);

        Mockito.when(userModuleRepository.findById(id)).thenReturn(Optional.of(userModule));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(userModuleRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        UserModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserModule userModule = new UserModule();
        beans.updateObjectos(userModule, dtoOk);
        userModule.setStartedAt(beans.converterStringToDate(dtoOk.getStartedAtS()));
        userModule.setCompletedAt(beans.converterStringToDate(dtoOk.getCompletedAtS()));
        userModule.setUser(montarUser());

        userModule.setUser(montarUser());
        userModule.setModule(montarModule());

        UserModuleDTO n = new UserModuleDTO();
        beans.updateObjectos(n, userModule);
        n.setUserId(dtoOk.getUserId());
        n.setUserName(dtoOk.getUserName());
        n.setModuleId(dtoOk.getModuleId());
        n.setModuleTitle(dtoOk.getModuleTitle());
        n.setStartedAtS(dtoOk.getStartedAtS());
        n.setCompletedAtS(dtoOk.getCompletedAtS());

        UserModuleDTO novo = service.montarDTO(userModule);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
