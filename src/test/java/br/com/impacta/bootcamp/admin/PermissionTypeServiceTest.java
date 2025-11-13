package br.com.impacta.bootcamp.admin;

import br.com.impacta.bootcamp.admin.dto.PermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.admin.repository.PermissionTypeRepository;
import br.com.impacta.bootcamp.admin.service.impl.PermissionTypeServiceImpl;
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
public class PermissionTypeServiceTest {

    @InjectMocks
    private PermissionTypeServiceImpl service;

    @Mock
    private PermissionTypeRepository permissionTypeRepository;

    private Beans beans = new Beans();
    private static final Long id = 123L;
    private static final Long idErro = 2L;

    private PermissionTypeDTO montarNull() {
        return new PermissionTypeDTO();
    }

    private PermissionTypeDTO montarDtoOK() {
        PermissionTypeDTO dtoOK = new PermissionTypeDTO();

        dtoOK.setId(id);
        dtoOK.setId(123L);
        dtoOK.setDescricao("aaaaaaaaaa");
        return dtoOK;
    }

    private PermissionTypeDTO montarDtoOkLimite() {
        PermissionTypeDTO dtoLimite = new PermissionTypeDTO();

        dtoLimite.setDescricao("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return dtoLimite;
    }

    private PermissionTypeDTO montarDtoErroMin() {
        PermissionTypeDTO dtoErroMin = new PermissionTypeDTO();

        dtoErroMin.setDescricao("aaaaaaaaa");
        return dtoErroMin;
    }

    private PermissionTypeDTO montarErroMax() {
        PermissionTypeDTO dtoErroMax = new PermissionTypeDTO();

        dtoErroMax.setDescricao("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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
        PermissionTypeDTO dtoNull = montarNull();
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
        PermissionTypeDTO erroMin = montarDtoErroMin();
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
        PermissionTypeDTO erroMax = montarErroMax();
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
        PermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);

        permissionType.setId(idErro);

        List<PermissionType> permissionTypes = new ArrayList<>();
        permissionTypes.add(permissionType);
        Page<PermissionType> msPage = new PageImpl<>(permissionTypes);

        Mockito.when(permissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este permissionType cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        PermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);
        List<PermissionType> permissionTypes = new ArrayList<>();
        permissionTypes.add(permissionType);
        Page<PermissionType> msPage = new PageImpl<>(permissionTypes);
        Mockito.when(permissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(permissionTypeRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        PermissionTypeDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);

        List<PermissionType> permissionTypes = new ArrayList<>();
        permissionTypes.add(permissionType);

        Page<PermissionType> msPage = new PageImpl<>(permissionTypes);
        Mockito.when(permissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(permissionTypeRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        PermissionTypeDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);
        Mockito.when(permissionTypeRepository.findById(id)).thenReturn(Optional.of(permissionType));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("PermissionType não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        PermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);
        Mockito.when(permissionTypeRepository.findById(id)).thenReturn(Optional.of(permissionType));
        Assertions.assertEquals(permissionType, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        PermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);

        List<PermissionType> permissionTypes = new ArrayList<>();
        permissionTypes.add(permissionType);
        Page<PermissionType> msPage = new PageImpl<>(permissionTypes);

        Mockito.when(permissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(permissionTypeRepository.findById(id)).thenReturn(Optional.of(permissionType));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(permissionTypeRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        PermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);

        List<PermissionType> permissionTypes = new ArrayList<>();
        permissionTypes.add(permissionType);
        Page<PermissionType> msPage = new PageImpl<>(permissionTypes);

        Mockito.when(permissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(permissionTypeRepository.findById(id)).thenReturn(Optional.of(permissionType));
        Mockito.when(permissionTypeRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        PermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);

        Mockito.when(permissionTypeRepository.findById(id)).thenReturn(Optional.of(permissionType));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(permissionTypeRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        PermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        PermissionType permissionType = new PermissionType();
        beans.updateObjectos(permissionType, dtoOk);

        PermissionTypeDTO n = new PermissionTypeDTO();
        beans.updateObjectos(n, permissionType);

        PermissionTypeDTO novo = service.montarDTO(permissionType);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
