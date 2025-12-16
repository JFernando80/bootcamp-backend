package br.com.impacta.bootcamp.formacao.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.dto.ActivityDTO;
import br.com.impacta.bootcamp.formacao.model.Activity;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.formacao.model.Module;

import java.util.List;
import java.util.UUID;

public interface ActivityService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(ActivityDTO dto);

    void update(ActivityDTO dto);

    Activity findByIdInterno(UUID id);

    void delete(UUID id);

    ActivityDTO montarDTO(Activity entity);

    List<ActivityDTO> findAllByModule(Module module);
}
