package br.com.impacta.bootcamp.formacao;

import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.formacao.dto.ActivityDTO;
import br.com.impacta.bootcamp.formacao.model.Activity;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.repository.ActivityRepository;
import br.com.impacta.bootcamp.formacao.service.ModuleService;
import br.com.impacta.bootcamp.formacao.service.impl.ActivityServiceImpl;
import br.com.impacta.bootcamp.util.UtilTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @InjectMocks
    private ActivityServiceImpl service;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ModuleService moduleService;

    private Beans beans = new Beans();
    private static final UUID id = UUID.randomUUID();
    private static final UUID idErro = UUID.randomUUID();

    private ActivityDTO montarNull() {
        return new ActivityDTO();
    }

    private ActivityDTO montarDtoOK() {
        ActivityDTO dtoOK = new ActivityDTO();

        dtoOK.setId(id);
        dtoOK.setType("aaaaa");
        dtoOK.setConfigJson("aa");
        dtoOK.setMaxScore(1L);
        dtoOK.setPassingScore(1L);
        dtoOK.setCreatedAtS(beans.getDataPattern("+1d", "max", 2));
        dtoOK.setUpdatedAtS(beans.getDataPattern("+1d", "max", 2));
        dtoOK.setModuleDescription("aaaaaaaaaa");
        return dtoOK;
    }

    private ActivityDTO montarDtoOkLimite() {
        ActivityDTO dtoLimite = new ActivityDTO();

        dtoLimite.setType("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setConfigJson("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setMaxScore(100L);
        dtoLimite.setPassingScore(100L);
        dtoLimite.setModuleDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private ActivityDTO montarDtoErroMin() {
        ActivityDTO dtoErroMin = new ActivityDTO();

        dtoErroMin.setType("aaaa");
        dtoErroMin.setConfigJson("a");
        dtoErroMin.setMaxScore(0L);
        dtoErroMin.setPassingScore(0L);
        dtoErroMin.setModuleDescription("aaaaaaaaa");
        return dtoErroMin;
    }

    private ActivityDTO montarErroMax() {
        ActivityDTO dtoErroMax = new ActivityDTO();

        dtoErroMax.setType("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setConfigJson("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setMaxScore(101L);
        dtoErroMax.setPassingScore(101L);
        dtoErroMax.setModuleDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoErroMax;
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
        Mockito.when(moduleService.findByIdInterno(Mockito.any())).thenReturn(montarModule());
    }

    @Test
    public void tentarSalvarErroCamposNull() {
        Beans beans = new Beans();
        ActivityDTO dtoNull = montarNull();
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
        ActivityDTO erroMin = montarDtoErroMin();
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
        ActivityDTO erroMax = montarErroMax();
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
        ActivityDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);

        activity.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        activity.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));
        activity.setId(idErro);
        activity.setModule(montarModule());

        List<Activity> activitys = new ArrayList<>();
        activitys.add(activity);
        Page<Activity> msPage = new PageImpl<>(activitys);

        Mockito.when(activityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este activity cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        ActivityDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);
        activity.setModule(montarModule());

        List<Activity> activitys = new ArrayList<>();
        activitys.add(activity);
        Page<Activity> msPage = new PageImpl<>(activitys);
        Mockito.when(activityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(activityRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        ActivityDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);

        activity.setModule(montarModule());

        List<Activity> activitys = new ArrayList<>();
        activitys.add(activity);

        Page<Activity> msPage = new PageImpl<>(activitys);
        Mockito.when(activityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(activityRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        ActivityDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);
        Mockito.when(activityRepository.findById(id)).thenReturn(Optional.of(activity));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Activity não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        ActivityDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);
        Mockito.when(activityRepository.findById(id)).thenReturn(Optional.of(activity));
        Assertions.assertEquals(activity, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        ActivityDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);
        activity.setModule(montarModule());

        List<Activity> activitys = new ArrayList<>();
        activitys.add(activity);
        Page<Activity> msPage = new PageImpl<>(activitys);

        Mockito.when(activityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(activityRepository.findById(id)).thenReturn(Optional.of(activity));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(activityRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        ActivityDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);
        activity.setModule(montarModule());

        List<Activity> activitys = new ArrayList<>();
        activitys.add(activity);
        Page<Activity> msPage = new PageImpl<>(activitys);

        Mockito.when(activityRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(activityRepository.findById(id)).thenReturn(Optional.of(activity));
        Mockito.when(activityRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        ActivityDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);

        Mockito.when(activityRepository.findById(id)).thenReturn(Optional.of(activity));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(activityRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        ActivityDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Activity activity = new Activity();
        beans.updateObjectos(activity, dtoOk);
        activity.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        activity.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));

        activity.setModule(montarModule());

        ActivityDTO n = new ActivityDTO();
        beans.updateObjectos(n, activity);
        n.setModuleId(dtoOk.getModuleId());
        n.setModuleDescription(dtoOk.getModuleDescription());
        n.setCreatedAtS(dtoOk.getCreatedAtS());
        n.setUpdatedAtS(dtoOk.getUpdatedAtS());

        ActivityDTO novo = service.montarDTO(activity);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
