package br.com.impacta.bootcamp.formacao.repository;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface UserActivityRepository extends
        JpaRepository<UserActivity, UUID>,
        JpaSpecificationExecutor<UserActivity> {

    List<UserActivity> findAllByUser(User user);

    List<UserActivity> findAllByModule(Module module);
}
