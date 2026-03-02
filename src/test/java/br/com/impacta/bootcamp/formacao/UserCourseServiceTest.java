package br.com.impacta.bootcamp.formacao;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.formacao.dto.UserCourseDTO;
import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.model.UserCourse;
import br.com.impacta.bootcamp.formacao.repository.UserCourseRepository;
import br.com.impacta.bootcamp.formacao.service.CourseService;
import br.com.impacta.bootcamp.formacao.service.ModuleService;
import br.com.impacta.bootcamp.formacao.service.UserCourseService;
import br.com.impacta.bootcamp.formacao.service.impl.UserCourseServiceImpl;
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
public class UserCourseServiceTest {

    @InjectMocks
    private UserCourseServiceImpl service;

    @Mock
    private UserCourseRepository userCourseRepository;

    @Mock
    private ModuleService moduleService;

    @Mock
    private UserCourseService userCourseService;

    @Mock
    private CourseService courseService;

    @Mock
    private UserService userService;

    private Beans beans = new Beans();
    private static final UUID id = UUID.randomUUID();
    private static final UUID idErro = UUID.randomUUID();

    private UserCourseDTO montarNull() {
        return new UserCourseDTO();
    }

    private UserCourseDTO montarDtoOK() {
        UserCourseDTO dtoOK = new UserCourseDTO();

        dtoOK.setId(id);
        dtoOK.setStatus("aaa");
        dtoOK.setProgressPercent(123L);
        dtoOK.setCertificateIssuedAtS(beans.getDataPattern("+1d", "max", 2));
        dtoOK.setCertificateToken("aaa");
        dtoOK.setCertificateUrl("aaa");
        dtoOK.setUserName("aaa");
        dtoOK.setCourseDescription("aaaaaaaaaa");
        return dtoOK;
    }

    private UserCourseDTO montarDtoOkLimite() {
        UserCourseDTO dtoLimite = new UserCourseDTO();

        dtoLimite.setEnrolledAtS(beans.getDataPattern("", "max", 2));
        dtoLimite.setProgressPercent(-9223372036854775808L);
        dtoLimite.setLastActivityAtS(beans.getDataPattern("", "max", 2));
        dtoLimite.setCourseDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private UserCourseDTO montarDtoErroMin() {
        UserCourseDTO dtoErroMin = new UserCourseDTO();

        dtoErroMin.setProgressPercent(-9223372036854775808L);
        dtoErroMin.setCourseDescription("aaaaaaaaa");
        return dtoErroMin;
    }

    private UserCourseDTO montarErroMax() {
        UserCourseDTO dtoErroMax = new UserCourseDTO();

        dtoErroMax.setEnrolledAtS(beans.getDataPattern("+0d", "max", 20));
        dtoErroMax.setProgressPercent(-9223372036854775808L);
        dtoErroMax.setLastActivityAtS(beans.getDataPattern("+0d", "max", 20));
        dtoErroMax.setCourseDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoErroMax;
    }

    private User montarUser() {

        User user = new User();
        user.setName("aaa");
        return user;
    }

    private Course montarCourse() {

        Course course = new Course();
        course.setTitle("aaaaaaaaaa");
        course.setDescription("aaaaaaaaaa");
        return course;
    }

    @BeforeEach
    public  void setup() {
        ReflectionTestUtils.setField(service, "beans", new Beans());
        ReflectionTestUtils.setField(service, "offset", 10);
        Mockito.when(userService.findByIdInterno(Mockito.any())).thenReturn(montarUser());
        Mockito.when(courseService.findByIdInterno(Mockito.any())).thenReturn(montarCourse());
        Mockito.when(userCourseRepository.save(Mockito.any())).thenReturn(montarCourse());
    }

    @Test
    public void tentarSalvarErroCamposNull() {
        Beans beans = new Beans();
        UserCourseDTO dtoNull = montarNull();
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
        UserCourseDTO erroMin = montarDtoErroMin();
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
        UserCourseDTO erroMax = montarErroMax();
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
        UserCourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        UserCourse userCourse = new UserCourse();
        beans.updateObjectos(userCourse, dtoOk);

        userCourse.setEnrolledAt(beans.converterStringToDate(dtoOk.getEnrolledAtS()));
        userCourse.setCertificateIssuedAt(beans.converterStringToDate(dtoOk.getCertificateIssuedAtS()));
        userCourse.setLastActivityAt(beans.converterStringToDate(dtoOk.getLastActivityAtS()));
        userCourse.setId(idErro);
        userCourse.setUser(montarUser());
        userCourse.setCourse(montarCourse());

        List<UserCourse> userCourses = new ArrayList<>();
        userCourses.add(userCourse);
        Page<UserCourse> msPage = new PageImpl<>(userCourses);

        Mockito.when(userCourseRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este userCourse cadastrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        UserCourseDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        UserCourse userCourse = new UserCourse();
        beans.updateObjectos(userCourse, dtoOk);
        Mockito.when(userCourseRepository.findById(id)).thenReturn(Optional.of(userCourse));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("UserCourse não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        UserCourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserCourse userCourse = new UserCourse();
        beans.updateObjectos(userCourse, dtoOk);
        Mockito.when(userCourseRepository.findById(id)).thenReturn(Optional.of(userCourse));
        Assertions.assertEquals(userCourse, service.findByIdInterno(id));
    }

    @Test
    public void deleteOk() {
        UserCourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserCourse userCourse = new UserCourse();
        beans.updateObjectos(userCourse, dtoOk);

        Mockito.when(userCourseRepository.findById(id)).thenReturn(Optional.of(userCourse));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(userCourseRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        UserCourseDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserCourse userCourse = new UserCourse();
        beans.updateObjectos(userCourse, dtoOk);
        userCourse.setUser(montarUser());
        userCourse.setEnrolledAt(beans.converterStringToDate(dtoOk.getEnrolledAtS()));
        userCourse.setCertificateIssuedAt(beans.converterStringToDate(dtoOk.getCertificateIssuedAtS()));
        userCourse.setLastActivityAt(beans.converterStringToDate(dtoOk.getLastActivityAtS()));

        userCourse.setUser(montarUser());
        userCourse.setCourse(montarCourse());

        UserCourseDTO n = new UserCourseDTO();
        beans.updateObjectos(n, userCourse);
        n.setUserId(dtoOk.getUserId());
        n.setUserName(dtoOk.getUserName());
        n.setCourseId(dtoOk.getCourseId());
        n.setCourseDescription(dtoOk.getCourseDescription());
        n.setEnrolledAtS(dtoOk.getEnrolledAtS());
        n.setCertificateIssuedAtS(dtoOk.getCertificateIssuedAtS());
        n.setLastActivityAtS(dtoOk.getLastActivityAtS());

        UserCourseDTO novo = service.montarDTO(userCourse);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
