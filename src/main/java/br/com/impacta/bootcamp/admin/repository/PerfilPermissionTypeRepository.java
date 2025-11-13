package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PerfilPermissionTypeRepository extends
        JpaRepository<PerfilPermissionType, Long>,
        JpaSpecificationExecutor<PerfilPermissionType> {

    List<PerfilPermissionType> findAllByPermissionType(PermissionType permissionType);
}
