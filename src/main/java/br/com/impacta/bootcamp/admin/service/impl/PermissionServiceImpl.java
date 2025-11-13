package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.PermissionDTO;
import br.com.impacta.bootcamp.admin.model.Permission;
import br.com.impacta.bootcamp.admin.repository.PermissionRepository;
import br.com.impacta.bootcamp.admin.service.PermissionService;
import br.com.impacta.bootcamp.admin.specification.PermissionSpecification;
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
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        PermissionSpecification msCategoria = new PermissionSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<Permission> msPage = permissionRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(PermissionDTO dto) {
        isRepetido(dto);
        permissionRepository.save(montarEntity(dto));
    }

    @Override
    public void update(PermissionDTO dto) {
        updateInterno(dto);
    }

    @Override
    public Permission findByIdInterno(Long id) {
        return permissionRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("Permission não encontrado"));
    }

    @Override
    public PermissionDTO findBySerial(Long serial) {
        Permission permission = permissionRepository.findBySerial(serial);

        if (permission != null) {
            return montarDTO(permission);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        Permission entity = findByIdInterno(id);
        permissionRepository.delete(entity);
    }

    @Override
    public PermissionDTO montarDTO(Permission entity) {
        PermissionDTO dto = new PermissionDTO();
        beans.updateObjectos(dto, entity);

        return dto;
    }

    private Permission montarEntity(PermissionDTO dto) {
        Permission entity = new Permission();
        beans.updateObjectos(entity, dto);
        return entity ;
    }

    private void updateInterno(PermissionDTO dto) {
        isRepetido(dto);
        Permission entity = findByIdInterno(dto.getId());
        Permission updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            permissionRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(PermissionDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(PermissionDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    PermissionDTO dto1 = (PermissionDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este permission cadastrado");
            }
        }
    }
}
