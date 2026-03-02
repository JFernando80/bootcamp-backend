package br.com.impacta.bootcamp.admin;

import br.com.impacta.bootcamp.admin.dto.UserPermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.UserPermissionType;
import br.com.impacta.bootcamp.admin.repository.UserPermissionTypeRepository;
import br.com.impacta.bootcamp.admin.service.impl.UserPermissionTypeServiceImpl;
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

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UserPermissionTypeServiceTest {

    @InjectMocks
    private UserPermissionTypeServiceImpl service;

    @Mock
    private UserPermissionTypeRepository userPermissionTypeRepository;

    private Beans beans = new Beans();
    private static final Long id = 1L;
    private static final Long idErro = 2L;

    private UserPermissionTypeDTO montarNull() {
        return new UserPermissionTypeDTO();
    }

    private UserPermissionTypeDTO montarDtoOK() {
        UserPermissionTypeDTO dtoOK = new UserPermissionTypeDTO();

        dtoOK.setId(id);
        return dtoOK;
    }

    private UserPermissionTypeDTO montarDtoOkLimite() {
        UserPermissionTypeDTO dtoLimite = new UserPermissionTypeDTO();

        return dtoLimite;
    }

    private UserPermissionTypeDTO montarDtoErroMin() {
        UserPermissionTypeDTO dtoErroMin = new UserPermissionTypeDTO();

        return dtoErroMin;
    }

    private UserPermissionTypeDTO montarErroMax() {
        UserPermissionTypeDTO dtoErroMax = new UserPermissionTypeDTO();

        return dtoErroMax;
    }

    @BeforeEach
    public  void setup() {
        ReflectionTestUtils.setField(service, "beans", new Beans());
        ReflectionTestUtils.setField(service, "offset", 10);
    }

    @Test
    public void findByIdInternoErroIdNaoEncontrado() {
        UserPermissionTypeDTO dtoOk = montarDtoOkLimite();
        Beans beans = new Beans();
        UserPermissionType userPermissionType = new UserPermissionType();
        beans.updateObjectos(userPermissionType, dtoOk);
        Mockito.when(userPermissionTypeRepository.findById(id)).thenReturn(Optional.of(userPermissionType));
        try {
            service.findByIdInterno(idErro);
        } catch (BusinessRuleException e) {
            Assertions.assertEquals("UserPermissionType não encontrado", e.getMessage());
        }
    }

    @Test
    public void findByIdInternoOk() {
        UserPermissionTypeDTO dtoOk = montarDtoOK();
        Beans beans = new Beans();
        UserPermissionType userPermissionType = new UserPermissionType();
        beans.updateObjectos(userPermissionType, dtoOk);
        Mockito.when(userPermissionTypeRepository.findById(id)).thenReturn(Optional.of(userPermissionType));
        Assertions.assertEquals(userPermissionType, service.findByIdInterno(id));
    }


    @Test
    public void deleteOk() {
        UserPermissionTypeDTO dtoOk = montarDtoOK();
        dtoOk.setId(1L);
        Beans beans = new Beans();
        UserPermissionType userPermissionType = new UserPermissionType();
        beans.updateObjectos(userPermissionType, dtoOk);

        Mockito.when(userPermissionTypeRepository.findById(id)).thenReturn(Optional.of(userPermissionType));

        service.delete(dtoOk.getId());
        Collection<Invocation> invocations = Mockito.mockingDetails(userPermissionTypeRepository).getInvocations();

        Assertions.assertEquals(2, invocations.size());
    }

}
