package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.PerfilPermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.admin.model.Permission;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.admin.repository.PerfilPermissionTypeRepository;
import br.com.impacta.bootcamp.admin.service.PerfilPermissionTypeService;
import br.com.impacta.bootcamp.admin.service.PermissionService;
import br.com.impacta.bootcamp.admin.service.PermissionTypeService;
import br.com.impacta.bootcamp.admin.specification.PerfilPermissionTypeSpecification;
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
public class PerfilPermissionTypeServiceImpl implements PerfilPermissionTypeService {

    @Autowired
    private PerfilPermissionTypeRepository perfilPermissionTypeRepository;

    @Autowired
    private PermissionTypeService permissionTypeService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        PerfilPermissionTypeSpecification msCategoria = new PerfilPermissionTypeSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<PerfilPermissionType> msPage = perfilPermissionTypeRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(PerfilPermissionTypeDTO dto) {
        isRepetido(dto);
        perfilPermissionTypeRepository.save(montarEntity(dto));
    }

    @Override
    public void update(PerfilPermissionTypeDTO dto) {
        updateInterno(dto);
    }

    @Override
    public PerfilPermissionType findByIdInterno(Long id) {
        return perfilPermissionTypeRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("PerfilPermissionType não encontrado"));
    }

    @Override
    public void delete(Long id) {
        PerfilPermissionType entity = findByIdInterno(id);
        perfilPermissionTypeRepository.delete(entity);
    }

    @Override
    public PerfilPermissionTypeDTO montarDTO(PerfilPermissionType entity) {
        PerfilPermissionTypeDTO dto = new PerfilPermissionTypeDTO();
        beans.updateObjectos(dto, entity);

        dto.setPermissionPermissionDescription(entity.getPermission().getPermissionDescription());
        dto.setPermissionId(entity.getPermission().getId());

        dto.setPermissionTypeDescricao(entity.getPermissionType().getDescricao());
        dto.setPermissionTypeId(entity.getPermissionType().getId());

        return dto;
    }

    @Override
    public List<PerfilPermissionType> listFromTypePermissionInterno(PermissionType permissionType) {
        return perfilPermissionTypeRepository.findAllByPermissionType(permissionType);
    }

    private PerfilPermissionType montarEntity(PerfilPermissionTypeDTO dto) {
        PerfilPermissionType entity = new PerfilPermissionType();
        beans.updateObjectos(entity, dto);

        PermissionType type = permissionTypeService.findByIdInterno(dto.getPermissionTypeId());
        entity.setPermissionType(type);

        Permission permission = permissionService.findByIdInterno(dto.getPermissionId());
        entity.setPermission(permission);

        return entity ;
    }

    private void updateInterno(PerfilPermissionTypeDTO dto) {
        isRepetido(dto);
        PerfilPermissionType entity = findByIdInterno(dto.getId());
        PerfilPermissionType updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            perfilPermissionTypeRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(PerfilPermissionTypeDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(PerfilPermissionTypeDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteriaDTO = new SearchCriteriaDTO();
        criteriaDTO.setKey("permissionDescription");
        criteriaDTO.setValue(dto.getPermissionPermissionDescription());
        criteriaDTO.setOperation(SearchOperation.EQUAL.name());
        criteriaDTO.setClasses("permission");
        lista.add(criteriaDTO);

        criteriaDTO = new SearchCriteriaDTO();
        criteriaDTO.setKey("descricao");
        criteriaDTO.setValue(dto.getPermissionTypeDescricao());
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
                    PerfilPermissionTypeDTO dto1 = (PerfilPermissionTypeDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este perfilPermissionType cadastrado");
            }
        }
    }
}
