package br.com.impacta.bootcamp.formacao.repository;

import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ModuleRepository extends
        JpaRepository<Module, UUID>,
        JpaSpecificationExecutor<Module> {

    List<Module> findAllByCourse(Course course);
}
