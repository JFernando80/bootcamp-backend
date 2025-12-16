package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.UserPermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.model.UserPermissionType;
import br.com.impacta.bootcamp.admin.repository.UserPermissionTypeRepository;
import br.com.impacta.bootcamp.admin.service.PermissionTypeService;
import br.com.impacta.bootcamp.admin.service.UserPermissionTypeService;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.admin.specification.UserPermissionTypeSpecification;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserPermissionTypeServiceImpl implements UserPermissionTypeService {

    @Autowired
    private UserPermissionTypeRepository userPermissionTypeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionTypeService permissionTypeService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        UserPermissionTypeSpecification msCategoria = new UserPermissionTypeSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<UserPermissionType> msPage = userPermissionTypeRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(UserPermissionTypeDTO dto) {
        isRepetido(dto);
        userPermissionTypeRepository.save(montarEntity(dto));
    }

    @Override
    public void update(UserPermissionTypeDTO dto) {
        updateInterno(dto);
    }

    @Override
    public UserPermissionType findByIdInterno(Long id) {
        return userPermissionTypeRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("UserPermissionType não encontrado"));
    }

    @Override
    public void delete(Long id) {
        UserPermissionType entity = findByIdInterno(id);
        userPermissionTypeRepository.delete(entity);
    }

    @Override
    public UserPermissionTypeDTO montarDTO(UserPermissionType entity) {
        UserPermissionTypeDTO dto = new UserPermissionTypeDTO();
        beans.updateObjectos(dto, entity);

        dto.setUserName(entity.getUser().getName());
        dto.setUserEmail(entity.getUser().getEmail());

        dto.setPermissionTypeDescricao(entity.getPermissionType().getDescricao());
        dto.setPermissionTypeId(entity.getPermissionType().getId());

        return dto;
    }

    @Override
    public List<UserPermissionType> findAllByUserInterno(User user) {
        return userPermissionTypeRepository.findAllByUser(user);
    }

    private UserPermissionType montarEntity(UserPermissionTypeDTO dto) {
        UserPermissionType entity = new UserPermissionType();
        beans.updateObjectos(entity, dto);

        User user = userService.findByEmailInterno(dto.getUserEmail());
        entity.setUser(user);

        PermissionType permissionType = permissionTypeService.findByIdInterno(dto.getPermissionTypeId());
        entity.setPermissionType(permissionType);


        return entity ;
    }

    private void updateInterno(UserPermissionTypeDTO dto) {
        isRepetido(dto);
        UserPermissionType entity = findByIdInterno(dto.getId());
        UserPermissionType updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            userPermissionTypeRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(UserPermissionTypeDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(UserPermissionTypeDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteriaDTO = new SearchCriteriaDTO();
        criteriaDTO.setKey("email");
        criteriaDTO.setValue(dto.getUserName());
        criteriaDTO.setOperation(SearchOperation.EQUAL.name());
        criteriaDTO.setClasses("user");
        lista.add(criteriaDTO);

        criteriaDTO = new SearchCriteriaDTO();
        criteriaDTO.setKey("id");
        criteriaDTO.setValue(dto.getPermissionTypeId());
        criteriaDTO.setOperation(SearchOperation.EQUAL.name());
        criteriaDTO.setClasses("permissionType");
        lista.add(criteriaDTO);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    UserPermissionTypeDTO dto1 = (UserPermissionTypeDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este userPermissionType cadastrado");
            }
        }
    }
}
