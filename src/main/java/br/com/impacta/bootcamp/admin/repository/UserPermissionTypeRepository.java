package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.model.UserPermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserPermissionTypeRepository extends
        JpaRepository<UserPermissionType, Long>,
        JpaSpecificationExecutor<UserPermissionType> {

    List<UserPermissionType> findAllByUser(User user);
}
