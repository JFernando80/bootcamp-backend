package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionTypeRepository extends
        JpaRepository<PermissionType, Long>,
        JpaSpecificationExecutor<PermissionType> {

}
