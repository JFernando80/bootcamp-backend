package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionGroupRepository extends
        JpaRepository<PermissionGroup, Long>,
        JpaSpecificationExecutor<PermissionGroup> {

}
