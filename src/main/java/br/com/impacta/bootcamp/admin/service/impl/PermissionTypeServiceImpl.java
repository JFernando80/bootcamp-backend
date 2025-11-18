package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.PermissionTypeDTO;
import br.com.impacta.bootcamp.admin.model.PermissionGroup;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.admin.repository.PermissionTypeRepository;
import br.com.impacta.bootcamp.admin.service.PermissionGroupService;
import br.com.impacta.bootcamp.admin.service.PermissionTypeService;
import br.com.impacta.bootcamp.admin.specification.PermissionTypeSpecification;
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
public class PermissionTypeServiceImpl implements PermissionTypeService {

    @Autowired
    private PermissionTypeRepository permissionTypeRepository;

    @Autowired
    private PermissionGroupService permissionGroupService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        PermissionTypeSpecification msCategoria = new PermissionTypeSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<PermissionType> msPage = permissionTypeRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(PermissionTypeDTO dto) {
        isRepetido(dto);
        permissionTypeRepository.save(montarEntity(dto));
    }

    @Override
    public void update(PermissionTypeDTO dto) {
        updateInterno(dto);
    }

    @Override
    public PermissionType findByIdInterno(Long id) {
        return permissionTypeRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("PermissionType não encontrado"));
    }

    @Override
    public void delete(Long id) {
        PermissionType entity = findByIdInterno(id);
        permissionTypeRepository.delete(entity);
    }

    @Override
    public PermissionTypeDTO montarDTO(PermissionType entity) {
        PermissionTypeDTO dto = new PermissionTypeDTO();
        beans.updateObjectos(dto, entity);

        dto.setPermissionGroupId(entity.getPermissionGroup().getId());
        dto.setPermissionGroupDescricao(entity.getPermissionGroup().getDescricao());

        return dto;
    }

    private PermissionType montarEntity(PermissionTypeDTO dto) {
        PermissionType entity = new PermissionType();
        beans.updateObjectos(entity, dto);

        PermissionGroup group = permissionGroupService.findByIdInterno(dto.getPermissionGroupId());
        entity.setPermissionGroup(group);

        return entity ;
    }

    private void updateInterno(PermissionTypeDTO dto) {
        isRepetido(dto);
        PermissionType entity = findByIdInterno(dto.getId());
        PermissionType updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            permissionTypeRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(PermissionTypeDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(PermissionTypeDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("descricao");
        criteria.setValue(dto.getDescricao());
        criteria.setOperation(SearchOperation.EQUAL.name());
        lista.add(criteria);

        criteria = new SearchCriteriaDTO();
        criteria.setKey("descricao");
        criteria.setValue(dto.getPermissionGroupDescricao());
        criteria.setOperation(SearchOperation.EQUAL.name());
        criteria.setClasses("permissionGroup");
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    PermissionTypeDTO dto1 = (PermissionTypeDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este permissionType cadastrado");
            }
        }
    }
}
