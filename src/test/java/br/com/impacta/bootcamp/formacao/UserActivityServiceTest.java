//package br.com.impacta.bootcamp.formacao;
//
//import br.com.impacta.bootcamp.commons.util.Util;
//import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
//import br.com.impacta.bootcamp.commons.util.Beans;
//import br.com.impacta.bootcamp.commons.util.Validation;
//import br.com.impacta.bootcamp.formacao.dto.UserActivityDTO;
//import br.com.impacta.bootcamp.formacao.model.UserActivity;
//import br.com.impacta.bootcamp.formacao.repository.UserActivityRepository;
//import br.com.impacta.bootcamp.formacao.service.impl.UserActivityServiceImpl;
//import br.com.impacta.bootcamp.util.UtilTest;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.invocation.Invocation;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.lang.reflect.Field;
//import java.util.*;
//
//@MockitoSettings(strictness = Strictness.LENIENT)
//@ExtendWith(MockitoExtension.class)
//public class UserActivityServiceTest {
//
//    @InjectMocks
//    private UserActivityServiceImpl service;
//
//    @Mock
//    private UserActivityRepository userActivityRepository;
//
//    private Beans beans = new Beans();
//    private static final UUID id = UUID.randomUUID();
//    private static final UUID idErro = UUID.randomUUID();
//
//    private UserActivityDTO montarNull() {
//        return new UserActivityDTO();
//    }
//
//    private UserActivityDTO montarDtoOK() {
//        UserActivityDTO dtoOK = new UserActivityDTO();
//
//        dtoOK.setId(id);
//        dtoOK.setAttemptNumber(123L);
//        dtoOK.setAnswerJson("aaa");
//        dtoOK.setScore(123L);
//        dtoOK.setSubmittedAtS(beans.getDataPattern("+1d", "max", 2));
//        dtoOK.setStatus("aa");
//        return dtoOK;
//    }
//
//    private UserActivityDTO montarDtoOkLimite() {
//        UserActivityDTO dtoLimite = new UserActivityDTO();
//
//        dtoLimite.setStatus("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        return dtoLimite;
//    }
//
//    private UserActivityDTO montarDtoErroMin() {
//        UserActivityDTO dtoErroMin = new UserActivityDTO();
//
//        dtoErroMin.setStatus("a");
//        return dtoErroMin;
//    }
//
//    private UserActivityDTO montarErroMax() {
//        UserActivityDTO dtoErroMax = new UserActivityDTO();
//
//        dtoErroMax.setStatus("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        return dtoErroMax;
//    }
//
//    @BeforeEach
//    public  void setup() {
//        ReflectionTestUtils.setField(service, "beans", new Beans());
//        ReflectionTestUtils.setField(service, "offset", 10);
//    }
//
//    @Test
//    public void tentarSalvarErroCamposNull() {
//        Beans beans = new Beans();
//        UserActivityDTO dtoNull = montarNull();
//        try {
//            service.save(dtoNull);
//        } catch (BusinessRuleException e) {
//            Field[] fields = dtoNull.getClass().getDeclaredFields();
//            List<Field> obrigatorios = new ArrayList<>();
//            for (Field field : fields) {
//                String campo = Util.toSneakCase(field.getName());
//                String hash = beans.toHexString(campo);
//                for (String erro : e.erros) {
//
//                    if (erro.contains(hash)) {
//                        if (Util.isTipoDTO(field)) {
//                            Assertions.assertEquals("A classe "+campo+" nao pode ser nula. "+hash, erro);
//                        } else {
//                            Assertions.assertEquals("O campo "+campo+" é obrigatório. "+hash, erro);
//                        }
//                    }
//                }
//
//                if (UtilTest.olharAnotacaoRequired(field)) {
//                    obrigatorios.add(field);
//                }
//            }
//
//            Assertions.assertEquals(e.erros.size(), obrigatorios.size());
//        }
//    }
//
//    @Test
//    public void tentarSalvarErroCamposMinimo() {
//        Beans beans = new Beans();
//        UserActivityDTO erroMin = montarDtoErroMin();
//        try {
//            service.save(erroMin);
//        } catch (BusinessRuleException e) {
//            Field[] fields = erroMin.getClass().getDeclaredFields();
//            List<Field> obrigatorios = new ArrayList<>();
//            for (Field field : fields) {
//                String campo = Util.toSneakCase(field.getName());
//                String hash = beans.toHexString(campo);
//                Validation valid = field.getDeclaredAnnotation(Validation.class);
//                for (String erro : e.erros) {
//                    if (erro.contains(hash)) {
//                        Long minValue = UtilTest.olharAnotacaoLengthMin(field);
//                        if (field.getType().equals(Long.class)) {
//                            Assertions.assertEquals("O valor do campo "+campo+" deve ser maior que "+minValue+". "+hash, erro);
//                        } else if (valid  != null && !valid.dateMin().equalsIgnoreCase("")) {
//                            Assertions.assertEquals("O campo "+campo+" é menor que o permitido. Data Minima maior que "+beans.converterDateToString(beans.validarDateMin(valid.dateMin(), 0))+". "+hash, erro);
//                        } else if (field.getType().equals(String.class)) {
//                            Assertions.assertEquals("O campo "+campo+" deve ter mais que "+minValue+" caracter/es. "+hash, erro);
//                        }
//                    }
//                }
//
//                if (UtilTest.olharAnotacaoMin(field)) {
//                    obrigatorios.add(field);
//                }
//            }
//
//            Assertions.assertEquals(e.erros.size(), obrigatorios.size());
//        }
//    }
//
//    @Test
//    public void tentarSalvarErroCamposMaximo() {
//        Beans beans = new Beans();
//        UserActivityDTO erroMax = montarErroMax();
//        try {
//            service.save(erroMax);
//        } catch (BusinessRuleException e) {
//            Field[] fields = erroMax.getClass().getDeclaredFields();
//            List<Field> obrigatorios = new ArrayList<>();
//            for (Field field : fields) {
//                String campo = Util.toSneakCase(field.getName());
//                String hash = beans.toHexString(campo);
//                Validation valid = field.getDeclaredAnnotation(Validation.class);
//                for (String erro : e.erros) {
//                    if (erro.contains(hash)) {
//                        Long maxValue = UtilTest.olharAnotacaoLengthMax(field);
//                        if (field.getType().equals(Long.class)) {
//                            Assertions.assertEquals("O valor do campo "+campo+" deve ser menor que "+maxValue+". "+hash, erro);
//                        } else if (valid  != null && !valid.dateMax().equalsIgnoreCase("")) {
//                            Assertions.assertEquals("O campo "+campo+" é maior que o permitido. Data Máxima: "+beans.converterDateToString(beans.validarDateMax(valid.dateMax(), 0))+". "+hash, erro);
//                        } else if (field.getType().equals(String.class)) {
//                            Assertions.assertEquals("O campo "+campo+" deve ter menos que "+maxValue+" caracter/es. "+hash, erro);
//                        }
//                    }
//                }
//
//                if (UtilTest.olharAnotacaoMax(field)) {
//                    obrigatorios.add(field);
//                }
//            }
//
//            Assertions.assertEquals(e.erros.size(), obrigatorios.size());
//        }
//    }
//
//    @Test
//    public void tentarSalvarErroRepetido() {
//        UserActivityDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//
//        userActivity.setSubmittedAt(beans.converterStringToDate(dtoOk.getSubmittedAtS()));
//        userActivity.setId(idErro);
//
//        List<UserActivity> userActivitys = new ArrayList<>();
//        userActivitys.add(userActivity);
//        Page<UserActivity> msPage = new PageImpl<>(userActivitys);
//
//        Mockito.when(userActivityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        try {
//            service.save(dtoOk);
//        } catch (BusinessRuleException e) {
//            Assertions.assertEquals("Já existe este userActivity cadastrado", e.getMessage());
//        }
//    }
//
//    @Test
//    public void tentarSalvarOK() {
//        UserActivityDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//        List<UserActivity> userActivitys = new ArrayList<>();
//        userActivitys.add(userActivity);
//        Page<UserActivity> msPage = new PageImpl<>(userActivitys);
//        Mockito.when(userActivityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        service.save(dtoOk);
//
//        Collection<Invocation> invocations = Mockito.mockingDetails(userActivityRepository).getInvocations();
//        Assertions.assertEquals(3, invocations.size());
//    }
//
//    @Test
//    public void tentarSalvarOKLimite() {
//        UserActivityDTO dtoOk = montarDtoOkLimite();
//        Beans beans = new Beans();
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//
//        List<UserActivity> userActivitys = new ArrayList<>();
//        userActivitys.add(userActivity);
//
//        Page<UserActivity> msPage = new PageImpl<>(userActivitys);
//        Mockito.when(userActivityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        service.save(dtoOk);
//
//        Collection<Invocation> invocations = Mockito.mockingDetails(userActivityRepository).getInvocations();
//        Assertions.assertEquals(3, invocations.size());
//    }
//
//    @Test
//    public void findByIdInternoErroIdNaoEncontrado() {
//        UserActivityDTO dtoOk = montarDtoOkLimite();
//        Beans beans = new Beans();
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//        Mockito.when(userActivityRepository.findById(id)).thenReturn(Optional.of(userActivity));
//        try {
//            service.findByIdInterno(idErro);
//        } catch (BusinessRuleException e) {
//            Assertions.assertEquals("UserActivity não encontrado", e.getMessage());
//        }
//    }
//
//    @Test
//    public void findByIdInternoOk() {
//        UserActivityDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//        Mockito.when(userActivityRepository.findById(id)).thenReturn(Optional.of(userActivity));
//        Assertions.assertEquals(userActivity, service.findByIdInterno(id));
//    }
//
//    @Test
//    public void updateOk() {
//        UserActivityDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//
//        List<UserActivity> userActivitys = new ArrayList<>();
//        userActivitys.add(userActivity);
//        Page<UserActivity> msPage = new PageImpl<>(userActivitys);
//
//        Mockito.when(userActivityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        Mockito.when(userActivityRepository.findById(id)).thenReturn(Optional.of(userActivity));
//
//        service.update(dtoOk);
//        Collection<Invocation> invocations = Mockito.mockingDetails(userActivityRepository).getInvocations();
//        Assertions.assertEquals(4, invocations.size());
//    }
//
//    @Test
//    public void updateErro() {
//        UserActivityDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//
//        List<UserActivity> userActivitys = new ArrayList<>();
//        userActivitys.add(userActivity);
//        Page<UserActivity> msPage = new PageImpl<>(userActivitys);
//
//        Mockito.when(userActivityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        Mockito.when(userActivityRepository.findById(id)).thenReturn(Optional.of(userActivity));
//        Mockito.when(userActivityRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));
//
//        try {
//            service.update(dtoOk);
//        } catch (Exception e) {
//            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
//        }
//    }
//
//    @Test
//    public void deleteOk() {
//        UserActivityDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//
//        Mockito.when(userActivityRepository.findById(id)).thenReturn(Optional.of(userActivity));
//
//        service.delete(dtoOk.getId());
//        Collection<Invocation> invocations = Mockito.mockingDetails(userActivityRepository).getInvocations();
//
//        Assertions.assertEquals(2, invocations.size());
//    }
//
//    @Test
//    public void montarDTOOk() {
//        UserActivityDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        UserActivity userActivity = new UserActivity();
//        beans.updateObjectos(userActivity, dtoOk);
//        userActivity.setSubmittedAt(beans.converterStringToDate(dtoOk.getSubmittedAtS()));
//
//        UserActivityDTO n = new UserActivityDTO();
//        beans.updateObjectos(n, userActivity);
//        n.setSubmittedAtS(dtoOk.getSubmittedAtS());
//
//        UserActivityDTO novo = service.montarDTO(userActivity);
//
//        Assertions.assertEquals(dtoOk, novo);
//        Assertions.assertEquals(dtoOk, n);
//    }
//
//}
