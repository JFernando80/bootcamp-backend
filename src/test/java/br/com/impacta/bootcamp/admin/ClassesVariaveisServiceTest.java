package br.com.impacta.bootcamp.admin;

import br.com.impacta.bootcamp.admin.automatizar.Util;
import br.com.impacta.bootcamp.admin.dto.ClassesVariaveisDTO;
import br.com.impacta.bootcamp.admin.model.ClassesVariaveis;
import br.com.impacta.bootcamp.admin.repository.ClassesVariaveisRepository;
import br.com.impacta.bootcamp.admin.service.impl.ClassesVariaveisServiceImpl;
import br.com.impacta.bootcamp.commons.enums.Status;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.util.UtilTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ClassesVariaveisServiceTest {

    @InjectMocks
    private ClassesVariaveisServiceImpl service;

    @Mock
    private ClassesVariaveisRepository classesVariaveisRepository;

    private Beans beans = new Beans();
    private static final Long id = 1L;
    private static final Long idErro = 2L;

    private ClassesVariaveisDTO montarNull() {
        return new ClassesVariaveisDTO();
    }

    private ClassesVariaveisDTO montarDtoOK() {
        ClassesVariaveisDTO dtoOK = new ClassesVariaveisDTO();

        dtoOK.setId(id);
        dtoOK.setId(123L);
        dtoOK.setVariavel("aa");
        dtoOK.setTipo("aaa");
        dtoOK.setStatus(Status.ATIVO);
        dtoOK.setHeader("aaa");
        return dtoOK;
    }

    private ClassesVariaveisDTO montarDtoOkLimite() {
        ClassesVariaveisDTO dtoLimite = new ClassesVariaveisDTO();

        dtoLimite.setVariavel("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setTipo("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setStatus(Status.ATIVO);
        return dtoLimite;
    }

    private ClassesVariaveisDTO montarDtoErroMin() {
        ClassesVariaveisDTO dtoErroMin = new ClassesVariaveisDTO();

        dtoErroMin.setVariavel("a");
        dtoErroMin.setTipo("aa");
        dtoErroMin.setStatus(Status.ATIVO);
        return dtoErroMin;
    }

    private ClassesVariaveisDTO montarErroMax() {
        ClassesVariaveisDTO dtoErroMax = new ClassesVariaveisDTO();

        dtoErroMax.setVariavel("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setTipo("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setStatus(Status.ATIVO);
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
        ClassesVariaveisDTO dtoNull = montarNull();
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
        ClassesVariaveisDTO erroMin = montarDtoErroMin();
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
        ClassesVariaveisDTO erroMax = montarErroMax();
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
    public void findByIdInternoErroIdNaoEncontrado() {
        ClassesVariaveisDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        ClassesVariaveis classesVariaveis = new ClassesVariaveis();
        beans.updateObjectos(classesVariaveis, dtoOk);
        Mockito.when(classesVariaveisRepository.findById(id)).thenReturn(Optional.of(classesVariaveis));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("ClassesVariaveis não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        ClassesVariaveisDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        ClassesVariaveis classesVariaveis = new ClassesVariaveis();
        beans.updateObjectos(classesVariaveis, dtoOk);
        Mockito.when(classesVariaveisRepository.findById(id)).thenReturn(Optional.of(classesVariaveis));
        Assertions.assertEquals(classesVariaveis, service.findByIdInterno(id));
    }

}
