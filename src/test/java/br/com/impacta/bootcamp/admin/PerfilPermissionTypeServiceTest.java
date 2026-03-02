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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Optional;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
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
        dtoOK.setId(123L);
        dtoOK.setPermissionTypeDescricao("aaa");
        dtoOK.setPermissionTypeId(123L);
        dtoOK.setPermissionPermissionDescription("aaa");
        dtoOK.setPermissionId(123L);
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


}
