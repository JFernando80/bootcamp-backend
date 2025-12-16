package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClassesRepository extends
        JpaRepository<Classes, Long>,
        JpaSpecificationExecutor<Classes> {

    Optional<Classes> findByName(String name);

    Optional<Classes> findBySimpleName(String name);
}
