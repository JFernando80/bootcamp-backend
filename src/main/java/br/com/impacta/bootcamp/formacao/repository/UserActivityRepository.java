package br.com.impacta.bootcamp.formacao.repository;

import br.com.impacta.bootcamp.formacao.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface UserActivityRepository extends
        JpaRepository<UserActivity, UUID>,
        JpaSpecificationExecutor<UserActivity> {

}
