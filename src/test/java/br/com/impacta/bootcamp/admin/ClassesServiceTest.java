package br.com.impacta.bootcamp.admin;

import br.com.impacta.bootcamp.admin.dto.ClassesDTO;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.repository.ClassesRepository;
import br.com.impacta.bootcamp.admin.service.impl.ClassesServiceImpl;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.util.Validation;
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

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ClassesServiceTest {

    @InjectMocks
    private ClassesServiceImpl service;

    @Mock
    private ClassesRepository classesRepository;

    private Beans beans = new Beans();
    private static final Long id = 123L;
    private static final Long idErro = 2L;

    private ClassesDTO montarNull() {
        return new ClassesDTO();
    }

    private ClassesDTO montarDtoOK() {
        ClassesDTO dtoOK = new ClassesDTO();

        dtoOK.setId(id);
        dtoOK.setId(123L);
        dtoOK.setSimpleName("aaaaaaaaaa");
        return dtoOK;
    }

    private ClassesDTO montarDtoOkLimite() {
        ClassesDTO dtoLimite = new ClassesDTO();

        dtoLimite.setSimpleName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private ClassesDTO montarDtoErroMin() {
        ClassesDTO dtoErroMin = new ClassesDTO();

        dtoErroMin.setSimpleName("aa");
        return dtoErroMin;
    }

    private ClassesDTO montarErroMax() {
        ClassesDTO dtoErroMax = new ClassesDTO();

        dtoErroMax.setSimpleName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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
        ClassesDTO dtoNull = montarNull();
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
        ClassesDTO erroMin = montarDtoErroMin();
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
        ClassesDTO erroMax = montarErroMax();
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
        ClassesDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);

        classes.setId(idErro);

        List<Classes> classess = new ArrayList<>();
        classess.add(classes);
        Page<Classes> msPage = new PageImpl<>(classess);

        Mockito.when(classesRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este classes cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        ClassesDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);
        List<Classes> classess = new ArrayList<>();
        classess.add(classes);
        Page<Classes> msPage = new PageImpl<>(classess);
        Mockito.when(classesRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(classesRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        ClassesDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);

        List<Classes> classess = new ArrayList<>();
        classess.add(classes);

        Page<Classes> msPage = new PageImpl<>(classess);
        Mockito.when(classesRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(classesRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        ClassesDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);
        Mockito.when(classesRepository.findById(id)).thenReturn(Optional.of(classes));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Classes não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        ClassesDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);
        Mockito.when(classesRepository.findById(id)).thenReturn(Optional.of(classes));
        Assertions.assertEquals(classes, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        ClassesDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);

        List<Classes> classess = new ArrayList<>();
        classess.add(classes);
        Page<Classes> msPage = new PageImpl<>(classess);

        Mockito.when(classesRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(classesRepository.findById(id)).thenReturn(Optional.of(classes));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(classesRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        ClassesDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);

        List<Classes> classess = new ArrayList<>();
        classess.add(classes);
        Page<Classes> msPage = new PageImpl<>(classess);

        Mockito.when(classesRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(classesRepository.findById(id)).thenReturn(Optional.of(classes));
        Mockito.when(classesRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        ClassesDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);

        Mockito.when(classesRepository.findById(id)).thenReturn(Optional.of(classes));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(classesRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        ClassesDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Classes classes = new Classes();
        beans.updateObjectos(classes, dtoOk);

        ClassesDTO n = new ClassesDTO();
        beans.updateObjectos(n, classes);

        ClassesDTO novo = service.montarDTO(classes);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
