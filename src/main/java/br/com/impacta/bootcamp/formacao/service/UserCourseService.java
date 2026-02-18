package br.com.impacta.bootcamp.formacao.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.dto.UserCourseDTO;
import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.model.UserCourse;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;
import java.util.UUID;

public interface UserCourseService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(UserCourseDTO dto);

    void update(UserCourseDTO dto);

    UserCourse findByIdInterno(UUID id);

    void delete(UUID id);

    UserCourseDTO montarDTO(UserCourse entity);

    void finalizarCourse(Course course, User user);

    void atualizarPercentual(Course course, User user, double total, double feitas);
}
