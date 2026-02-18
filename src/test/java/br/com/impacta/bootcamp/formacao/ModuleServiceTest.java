package br.com.impacta.bootcamp.formacao;

import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Util;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.formacao.dto.ModuleDTO;
import br.com.impacta.bootcamp.formacao.enums.StatusCourse;
import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.repository.ModuleRepository;
import br.com.impacta.bootcamp.formacao.service.CourseService;
import br.com.impacta.bootcamp.formacao.service.impl.ModuleServiceImpl;
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
public class ModuleServiceTest {

    @InjectMocks
    private ModuleServiceImpl service;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseService courseService;

    private Beans beans = new Beans();
    private static final UUID id = UUID.randomUUID();
    private static final UUID idErro = UUID.randomUUID();

    private ModuleDTO montarNull() {
        return new ModuleDTO();
    }

    private ModuleDTO montarDtoOK() {
        ModuleDTO dtoOK = new ModuleDTO();

        dtoOK.setId(id);
        dtoOK.setIndex(10L);
        dtoOK.setTitle("aaaaaaaaaa");
        dtoOK.setDescription("aaaaaaaaaa");
        dtoOK.setCourseDescription("aaaaaaaaaa");
        return dtoOK;
    }

    private ModuleDTO montarDtoOkLimite() {
        ModuleDTO dtoLimite = new ModuleDTO();

        dtoLimite.setIndex(99L);
        dtoLimite.setTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoLimite.setCreatedAtS(beans.getDataPattern("", "max", 2));
        dtoLimite.setUpdatedAtS(beans.getDataPattern("", "max", 2));
        dtoLimite.setCourseDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private ModuleDTO montarDtoErroMin() {
        ModuleDTO dtoErroMin = new ModuleDTO();

        dtoErroMin.setIndex(-1L);
        dtoErroMin.setTitle("aaaaaaaaa");
        dtoErroMin.setDescription("aaaaaaaaa");
        dtoErroMin.setCourseDescription("aaaaaaaaa");
        return dtoErroMin;
    }

    private ModuleDTO montarErroMax() {
        ModuleDTO dtoErroMax = new ModuleDTO();

        dtoErroMax.setIndex(123L);
        dtoErroMax.setTitle("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        dtoErroMax.setCreatedAtS(beans.getDataPattern("+0d", "max", 20));
        dtoErroMax.setUpdatedAtS(beans.getDataPattern("+0d", "max", 20));
        dtoErroMax.setCourseDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoErroMax;
    }

    private Course montarCourse() {

        Course course = new Course();
        course.setTitle("aaaaaaaaaa");
        course.setDescription("aaaaaaaaaa");
        course.setStatus(StatusCourse.DRAFT.name());
        return course;
    }

    @BeforeEach
    public  void setup() {
        ReflectionTestUtils.setField(service, "beans", new Beans());
        ReflectionTestUtils.setField(service, "offset", 10);
        Mockito.when(courseService.findByIdInterno(Mockito.any())).thenReturn(montarCourse());
    }

    @Test
    public void tentarSalvarErroCamposNull() {
        Beans beans = new Beans();
        ModuleDTO dtoNull = montarNull();
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
        ModuleDTO erroMin = montarDtoErroMin();
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
        ModuleDTO erroMax = montarErroMax();
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
        ModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Module module = new Module();
        beans.updateObjectos(module, dtoOk);

        module.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        module.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));
        module.setId(idErro);
        module.setCourse(montarCourse());

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        Page<Module> msPage = new PageImpl<>(modules);

        Mockito.when(moduleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este module cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        ModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Module module = new Module();
        beans.updateObjectos(module, dtoOk);
        module.setCourse(montarCourse());

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        Page<Module> msPage = new PageImpl<>(modules);
        Mockito.when(moduleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(moduleRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        ModuleDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Module module = new Module();
        beans.updateObjectos(module, dtoOk);

        module.setCourse(montarCourse());

        List<Module> modules = new ArrayList<>();
        modules.add(module);

        Page<Module> msPage = new PageImpl<>(modules);
        Mockito.when(moduleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(moduleRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        ModuleDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        Module module = new Module();
        beans.updateObjectos(module, dtoOk);
        Mockito.when(moduleRepository.findById(id)).thenReturn(Optional.of(module));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Module não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        ModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Module module = new Module();
        beans.updateObjectos(module, dtoOk);
        Mockito.when(moduleRepository.findById(id)).thenReturn(Optional.of(module));
        Assertions.assertEquals(module, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        ModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Module module = new Module();
        beans.updateObjectos(module, dtoOk);
        module.setCourse(montarCourse());

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        Page<Module> msPage = new PageImpl<>(modules);

        Mockito.when(moduleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(moduleRepository.findById(id)).thenReturn(Optional.of(module));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(moduleRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        ModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        Module module = new Module();
        beans.updateObjectos(module, dtoOk);
        module.setCourse(montarCourse());

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        Page<Module> msPage = new PageImpl<>(modules);

        Mockito.when(moduleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(moduleRepository.findById(id)).thenReturn(Optional.of(module));
        Mockito.when(moduleRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        ModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Module module = new Module();
        beans.updateObjectos(module, dtoOk);

        Mockito.when(moduleRepository.findById(id)).thenReturn(Optional.of(module));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(moduleRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        ModuleDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        Module module = new Module();
        beans.updateObjectos(module, dtoOk);
        module.setCreatedAt(beans.converterStringToDate(dtoOk.getCreatedAtS()));
        module.setUpdatedAt(beans.converterStringToDate(dtoOk.getUpdatedAtS()));

        module.setCourse(montarCourse());

        ModuleDTO n = new ModuleDTO();
        beans.updateObjectos(n, module);
        n.setCourseId(dtoOk.getCourseId());
        n.setCourseDescription(dtoOk.getCourseDescription());
        n.setCreatedAtS(dtoOk.getCreatedAtS());
        n.setUpdatedAtS(dtoOk.getUpdatedAtS());

        ModuleDTO novo = service.montarDTO(module);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
