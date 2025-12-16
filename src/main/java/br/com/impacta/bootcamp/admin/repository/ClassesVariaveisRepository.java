package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.model.ClassesVariaveis;
import br.com.impacta.bootcamp.commons.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ClassesVariaveisRepository extends
        JpaRepository<ClassesVariaveis, Long>,
        JpaSpecificationExecutor<ClassesVariaveis> {

    ClassesVariaveis findByClassesAndVariavel(Classes classes, String variavel);

    List<ClassesVariaveis> findAllByClassesOrderByVariavelAsc(Classes classes);

    List<ClassesVariaveis> findAllByClassesOrderByIdAsc(Classes classes);
}
