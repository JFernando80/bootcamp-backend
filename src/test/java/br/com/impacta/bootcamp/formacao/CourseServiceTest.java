package br.com.impacta.bootcamp.formacao;

import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.formacao.dto.CourseDTO;
import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.repository.CourseRepository;
import br.com.impacta.bootcamp.formacao.service.impl.CourseServiceImpl;
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
public class CourseServiceTest {

    @InjectMocks
    private CourseServiceImpl service;

    @Mock
    private CourseRepository courseRepository;

    private Beans beans = new Beans();
    private static final UUID id = UUID.randomUUID();
    private static final UUID idErro = UUID.randomUUID();

    private CourseDTO montarNull() {
        return new CourseDTO();
    }

    private CourseDTO montarDtoOK() {
        CourseDTO dtoOK = new CourseDTO();

        dtoOK.setId(id);
        dtoOK.setSlug("aaa");
        dtoOK.setTitle("aaaaaaaaaa");
        dtoOK.setDescription("aaaaaaaaaa");
        dtoOK.setPublishedAtS(beans.getDataPattern("+1d", "max", 2));
        dtoOK.setCreatedAtS(beans.getDataPattern("+1d", "max", 2));
        dtoOK.setUpdatedAtS(beans.getDataPattern("+1d", "max", 2));
        return dtoOK;
    }

    private CourseDTO montarDtoOkLimite() {
        CourseDTO dtoLimite = new CourseDTO();

        dtoLimite.setTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private CourseDTO montarDtoErroMin() {
        CourseDTO dtoErroMin = new CourseDTO();

        dtoErroMin.setTitle("aaaaaaaaa");
        dtoErroMin.setDescription("aaaaaaaaa");
        return dtoErroMin;
    }

    private CourseDTO montarErroMax() {
        CourseDTO dtoErroMax = new CourseDTO();

        dtoErroMax.setTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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
        CourseDTO dtoNull = montarNull();
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
        CourseDTO erroMin = montarDtoErroMin();
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
        CourseDTO erroMax = montarErroMax();
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
        CourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Course course = new Course();
        beans.updateObjectos(course, dtoOk);

        course.setPublishedAt(beans.converterStringToDate(dtoOk.getPublishedAtS()));
        course.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        course.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));
        course.setId(idErro);

        List<Course> courses = new ArrayList<>();
        courses.add(course);
        Page<Course> msPage = new PageImpl<>(courses);

        Mockito.when(courseRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este course cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        CourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Course course = new Course();
        beans.updateObjectos(course, dtoOk);
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        Page<Course> msPage = new PageImpl<>(courses);
        Mockito.when(courseRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(courseRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        CourseDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Course course = new Course();
        beans.updateObjectos(course, dtoOk);

        List<Course> courses = new ArrayList<>();
        courses.add(course);

        Page<Course> msPage = new PageImpl<>(courses);
        Mockito.when(courseRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(courseRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        CourseDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Course course = new Course();
        beans.updateObjectos(course, dtoOk);
        Mockito.when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Course não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        CourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Course course = new Course();
        beans.updateObjectos(course, dtoOk);
        Mockito.when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        Assertions.assertEquals(course, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        CourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Course course = new Course();
        beans.updateObjectos(course, dtoOk);

        List<Course> courses = new ArrayList<>();
        courses.add(course);
        Page<Course> msPage = new PageImpl<>(courses);

        Mockito.when(courseRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(courseRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        CourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Course course = new Course();
        beans.updateObjectos(course, dtoOk);

        List<Course> courses = new ArrayList<>();
        courses.add(course);
        Page<Course> msPage = new PageImpl<>(courses);

        Mockito.when(courseRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        Mockito.when(courseRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        CourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Course course = new Course();
        beans.updateObjectos(course, dtoOk);

        Mockito.when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(courseRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        CourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Course course = new Course();
        beans.updateObjectos(course, dtoOk);
        course.setPublishedAt(beans.converterStringToDate(dtoOk.getPublishedAtS()));
        course.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        course.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));

        CourseDTO n = new CourseDTO();
        beans.updateObjectos(n, course);
        n.setPublishedAtS(dtoOk.getPublishedAtS());
        n.setCreatedAtS(dtoOk.getCreatedAtS());
        n.setUpdatedAtS(dtoOk.getUpdatedAtS());

        CourseDTO novo = service.montarDTO(course);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
