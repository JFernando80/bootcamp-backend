//package br.com.impacta.bootcamp.admin;
//
//import br.com.impacta.bootcamp.admin.dto.PermissionDTO;
//import br.com.impacta.bootcamp.admin.model.Permission;
//import br.com.impacta.bootcamp.admin.repository.PermissionRepository;
//import br.com.impacta.bootcamp.admin.service.impl.PermissionServiceImpl;
//import br.com.impacta.bootcamp.commons.util.Util;
//import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
//import br.com.impacta.bootcamp.commons.util.Beans;
//import br.com.impacta.bootcamp.commons.util.Validation;
//import br.com.impacta.bootcamp.util.UtilTest;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.invocation.Invocation;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//public class PermissionServiceTest {
//
//    @InjectMocks
//    private PermissionServiceImpl service;
//
//    @Mock
//    private PermissionRepository permissionRepository;
//
//    private Beans beans = new Beans();
//    private static final Long id = 123L;
//    private static final Long idErro = 2L;
//
//    private PermissionDTO montarNull() {
//        return new PermissionDTO();
//    }
//
//    private PermissionDTO montarDtoOK() {
//        PermissionDTO dtoOK = new PermissionDTO();
//
//        dtoOK.setId(id);
//        dtoOK.setId(123L);
//        dtoOK.setPermission("aaaaaaaaaa");
//        dtoOK.setPermissionDescription("aaaaaaaaaa");
//        dtoOK.setScreen("aaa");
//        dtoOK.setSerial(123L);
//        return dtoOK;
//    }
//
//    private PermissionDTO montarDtoOkLimite() {
//        PermissionDTO dtoLimite = new PermissionDTO();
//
//        dtoLimite.setPermission("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        dtoLimite.setPermissionDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        dtoLimite.setSerial(-9223372036854775808L);
//        return dtoLimite;
//    }
//
//    private PermissionDTO montarDtoErroMin() {
//        PermissionDTO dtoErroMin = new PermissionDTO();
//
//        dtoErroMin.setPermission("aaaaaaaaa");
//        dtoErroMin.setPermissionDescription("aaaaaaaaa");
//        dtoErroMin.setSerial(-9223372036854775808L);
//        return dtoErroMin;
//    }
//
//    private PermissionDTO montarErroMax() {
//        PermissionDTO dtoErroMax = new PermissionDTO();
//
//        dtoErroMax.setPermission("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        dtoErroMax.setPermissionDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        dtoErroMax.setSerial(-9223372036854775808L);
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
//        PermissionDTO dtoNull = montarNull();
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
//        PermissionDTO erroMin = montarDtoErroMin();
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
//        PermissionDTO erroMax = montarErroMax();
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
//        PermissionDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//
//        permission.setId(idErro);
//
//        List<Permission> permissions = new ArrayList<>();
//        permissions.add(permission);
//        Page<Permission> msPage = new PageImpl<>(permissions);
//
//        Mockito.when(permissionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        try {
//            service.save(dtoOk);
//        } catch (BusinessRuleException e) {
//            Assertions.assertEquals("Já existe este permission cadastrado", e.getMessage());
//        }
//    }
//
//    @Test
//    public void tentarSalvarOK() {
//        PermissionDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//        List<Permission> permissions = new ArrayList<>();
//        permissions.add(permission);
//        Page<Permission> msPage = new PageImpl<>(permissions);
//        Mockito.when(permissionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        service.save(dtoOk);
//
//        Collection<Invocation> invocations = Mockito.mockingDetails(permissionRepository).getInvocations();
//        Assertions.assertEquals(3, invocations.size());
//    }
//
//    @Test
//    public void tentarSalvarOKLimite() {
//        PermissionDTO dtoOk = montarDtoOkLimite();
//        Beans beans = new Beans();
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//
//        List<Permission> permissions = new ArrayList<>();
//        permissions.add(permission);
//
//        Page<Permission> msPage = new PageImpl<>(permissions);
//        Mockito.when(permissionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        service.save(dtoOk);
//
//        Collection<Invocation> invocations = Mockito.mockingDetails(permissionRepository).getInvocations();
//        Assertions.assertEquals(3, invocations.size());
//    }
//
//    @Test
//    public void findByIdInternoErroIdNaoEncontrado() {
//        PermissionDTO dtoOk = montarDtoOkLimite();
//        Beans beans = new Beans();
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//        Mockito.when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
//        try {
//            service.findByIdInterno(idErro);
//        } catch (BusinessRuleException e) {
//            Assertions.assertEquals("Permission não encontrado", e.getMessage());
//        }
//    }
//
//    @Test
//    public void findByIdInternoOk() {
//        PermissionDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//        Mockito.when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
//        Assertions.assertEquals(permission, service.findByIdInterno(id));
//    }
//
//    @Test
//    public void updateOk() {
//        PermissionDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//
//        List<Permission> permissions = new ArrayList<>();
//        permissions.add(permission);
//        Page<Permission> msPage = new PageImpl<>(permissions);
//
//        Mockito.when(permissionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        Mockito.when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
//
//        service.update(dtoOk);
//        Collection<Invocation> invocations = Mockito.mockingDetails(permissionRepository).getInvocations();
//        Assertions.assertEquals(4, invocations.size());
//    }
//
//    @Test
//    public void updateErro() {
//        PermissionDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//
//        List<Permission> permissions = new ArrayList<>();
//        permissions.add(permission);
//        Page<Permission> msPage = new PageImpl<>(permissions);
//
//        Mockito.when(permissionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
//        Mockito.when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
//        Mockito.when(permissionRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));
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
//        PermissionDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//
//        Mockito.when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
//
//        service.delete(dtoOk.getId());
//        Collection<Invocation> invocations = Mockito.mockingDetails(permissionRepository).getInvocations();
//
//        Assertions.assertEquals(2, invocations.size());
//    }
//
//    @Test
//    public void montarDTOOk() {
//        PermissionDTO dtoOk = montarDtoOK();
//        Beans beans = new Beans();
//        Permission permission = new Permission();
//        beans.updateObjectos(permission, dtoOk);
//
//        PermissionDTO n = new PermissionDTO();
//        beans.updateObjectos(n, permission);
//
//        PermissionDTO novo = service.montarDTO(permission);
//
//        Assertions.assertEquals(dtoOk, novo);
//        Assertions.assertEquals(dtoOk, n);
//    }
//
//}
