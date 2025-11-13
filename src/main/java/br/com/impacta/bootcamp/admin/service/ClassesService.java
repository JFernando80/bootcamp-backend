package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesDTO;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;

public interface ClassesService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(ClassesDTO dto);

    void update(ClassesDTO dto);

    Classes findByIdInterno(Long id);

    void delete(Long id);

    ClassesDTO montarDTO(Classes entity);

    Classes findByName(String name);
}
