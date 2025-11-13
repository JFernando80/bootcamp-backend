package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesVariaveisDTO;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.model.ClassesVariaveis;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.model.Content;

import java.util.List;
import java.util.Locale;

public interface ClassesVariaveisService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(ClassesVariaveisDTO dto);

    void update(ClassesVariaveisDTO dto);

    ClassesVariaveis findByIdInterno(Long id);

    void delete(Long id);

    ClassesVariaveisDTO montarDTO(ClassesVariaveis entity);

    List<ClassesVariaveisDTO> findAllByClassesAndStatus(Classes classes, Locale locale);

    List<ClassesVariaveisDTO> findAllByClassesAndStatus(Classes classes, Content content);
}
