package br.com.impacta.bootcamp.formacao.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.formacao.dto.CourseDTO;
import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;
import java.util.UUID;

public interface CourseService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(CourseDTO dto);

    void update(CourseDTO dto);

    Course findByIdInterno(UUID id);

    void delete(UUID id);

    CourseDTO montarDTO(Course entity);

}
