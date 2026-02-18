package br.com.impacta.bootcamp.formacao.repository;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.model.UserModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface UserModuleRepository extends
        JpaRepository<UserModule, UUID>,
        JpaSpecificationExecutor<UserModule> {

    List<UserModule> findAllByUser(User user);
}
