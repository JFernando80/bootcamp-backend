package br.com.impacta.bootcamp.formacao.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.dto.ModuleDTO;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;
import java.util.UUID;

public interface ModuleService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(ModuleDTO dto);

    void update(ModuleDTO dto);

    Module findByIdInterno(UUID id);

    void delete(UUID id);

    ModuleDTO montarDTO(Module entity);

}
