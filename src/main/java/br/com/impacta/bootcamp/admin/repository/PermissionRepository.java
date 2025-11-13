package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PermissionRepository extends
        JpaRepository<Permission, Long>,
        JpaSpecificationExecutor<Permission> {

    Optional<Permission> findByPermissionDescription(String descriptioin);

    Permission findBySerial(Long serial);
}
