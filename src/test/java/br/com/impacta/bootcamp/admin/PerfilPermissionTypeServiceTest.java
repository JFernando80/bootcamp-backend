package br.com.impacta.bootcamp.admin;

import br.com.impacta.bootcamp.admin.dto.PerfilPermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.admin.repository.PerfilPermissionTypeRepository;
import br.com.impacta.bootcamp.admin.service.impl.PerfilPermissionTypeServiceImpl;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PerfilPermissionTypeServiceTest {

    @InjectMocks
    private PerfilPermissionTypeServiceImpl service;

    @Mock
    private PerfilPermissionTypeRepository perfilPermissionTypeRepository;

    private Beans beans = new Beans();
    private static final Long id = 1L;
    private static final Long idErro = 2L;

    private PerfilPermissionTypeDTO montarNull() {
        return new PerfilPermissionTypeDTO();
    }

    private PerfilPermissionTypeDTO montarDtoOK() {
        PerfilPermissionTypeDTO dtoOK = new PerfilPermissionTypeDTO();

        dtoOK.setId(id);
        return dtoOK;
    }

    private PerfilPermissionTypeDTO montarDtoOkLimite() {
        PerfilPermissionTypeDTO dtoLimite = new PerfilPermissionTypeDTO();

        return dtoLimite;
    }

    private PerfilPermissionTypeDTO montarDtoErroMin() {
        PerfilPermissionTypeDTO dtoErroMin = new PerfilPermissionTypeDTO();

        return dtoErroMin;
    }

    private PerfilPermissionTypeDTO montarErroMax() {
        PerfilPermissionTypeDTO dtoErroMax = new PerfilPermissionTypeDTO();

        return dtoErroMax;
    }

    @BeforeEach
    public  void setup() {
        ReflectionTestUtils.setField(service, "beans", new Beans());
        ReflectionTestUtils.setField(service, "offset", 10);
    }

    @Test
    public void tentarSalvarErroRepetido() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();

        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);

        perfilPermissionType.setId(idErro);

        List<PerfilPermissionType> perfilPermissionTypes = new ArrayList<>();
        perfilPermissionTypes.add(perfilPermissionType);
        Page<PerfilPermissionType> msPage = new PageImpl<>(perfilPermissionTypes);

        Mockito.when(perfilPermissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        try {
            service.save(dtoOk);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("Já existe este perfilPermissionType cadastrado", e.getMessage());
        }
    }

    @Test
    public void tentarSalvarOK() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);
        List<PerfilPermissionType> perfilPermissionTypes = new ArrayList<>();
        perfilPermissionTypes.add(perfilPermissionType);
        Page<PerfilPermissionType> msPage = new PageImpl<>(perfilPermissionTypes);
        Mockito.when(perfilPermissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(perfilPermissionTypeRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void tentarSalvarOKLimite() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);

        List<PerfilPermissionType> perfilPermissionTypes = new ArrayList<>();
        perfilPermissionTypes.add(perfilPermissionType);

        Page<PerfilPermissionType> msPage = new PageImpl<>(perfilPermissionTypes);
        Mockito.when(perfilPermissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        service.save(dtoOk);

        Collection<Invocation> invocations = Mockito.mockingDetails(perfilPermissionTypeRepository).getInvocations();
        Assertions.assertEquals(3, invocations.size());
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);
        Mockito.when(perfilPermissionTypeRepository.findById(id)).thenReturn(Optional.of(perfilPermissionType));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("PerfilPermissionType não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);
        Mockito.when(perfilPermissionTypeRepository.findById(id)).thenReturn(Optional.of(perfilPermissionType));
        Assertions.assertEquals(perfilPermissionType, service.findByIdInterno(id));
    }

    @Test
    public void updateOk() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOK();
        dtoOk.setId(1L);
        Beans beans = new Beans();

        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);

        List<PerfilPermissionType> perfilPermissionTypes = new ArrayList<>();
        perfilPermissionTypes.add(perfilPermissionType);
        Page<PerfilPermissionType> msPage = new PageImpl<>(perfilPermissionTypes);

        Mockito.when(perfilPermissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(perfilPermissionTypeRepository.findById(id)).thenReturn(Optional.of(perfilPermissionType));

        service.update(dtoOk);
        Collection<Invocation> invocations = Mockito.mockingDetails(perfilPermissionTypeRepository).getInvocations();
        Assertions.assertEquals(4, invocations.size());
    }

    @Test
    public void updateErro() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOK();
        dtoOk.setId(1L);
        Beans beans = new Beans();

        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);

        List<PerfilPermissionType> perfilPermissionTypes = new ArrayList<>();
        perfilPermissionTypes.add(perfilPermissionType);
        Page<PerfilPermissionType> msPage = new PageImpl<>(perfilPermissionTypes);

        Mockito.when(perfilPermissionTypeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);
        Mockito.when(perfilPermissionTypeRepository.findById(id)).thenReturn(Optional.of(perfilPermissionType));
        Mockito.when(perfilPermissionTypeRepository.save(Mockito.any())).thenThrow(new RuntimeException("erro entity"));

        try {
            service.update(dtoOk);
        } catch (Exception e) {
            Assertions.assertEquals("erro desconhecido: erro entity", e.getMessage());
        }
    }

    @Test
    public void deleteOk() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOK();
        dtoOk.setId(1L);
        Beans beans = new Beans();
        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);

        Mockito.when(perfilPermissionTypeRepository.findById(id)).thenReturn(Optional.of(perfilPermissionType));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(perfilPermissionTypeRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

    @Test
    public void montarDTOOk() {
        PerfilPermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        PerfilPermissionType perfilPermissionType = new PerfilPermissionType();
        beans.updateObjectos(perfilPermissionType, dtoOk);

        PerfilPermissionTypeDTO n = new PerfilPermissionTypeDTO();
        beans.updateObjectos(n, perfilPermissionType);

        PerfilPermissionTypeDTO novo = service.montarDTO(perfilPermissionType);

        Assertions.assertEquals(dtoOk, novo);
        Assertions.assertEquals(dtoOk, n);
    }

}
