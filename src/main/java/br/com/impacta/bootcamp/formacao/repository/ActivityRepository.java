package br.com.impacta.bootcamp.formacao.repository;

import br.com.impacta.bootcamp.formacao.model.Activity;
import br.com.impacta.bootcamp.formacao.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends
        JpaRepository<Activity, UUID>,
        JpaSpecificationExecutor<Activity> {

    List<Activity> findAllByModule(Module module);
}
