package br.com.impacta.bootcamp.commons.job;

import br.com.impacta.bootcamp.BootcampApplication;
import br.com.impacta.bootcamp.admin.dto.PermissionDTO;
import br.com.impacta.bootcamp.admin.service.PermissionService;
import br.com.impacta.bootcamp.commons.enums.PermissoesEnum;
import br.com.impacta.bootcamp.commons.util.Monitorar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class CriarPermissoes {

    @Autowired
    private PermissionService permissionService;

    @Monitorar
    @Scheduled(fixedDelay = 600000000, initialDelay = 500)
    public void automatizaCalendario() {
        if (BootcampApplication.executouPermissoes) {
            return;
        }

        for (int i = 0; i < PermissoesEnum.values().length; i++) {
            PermissoesEnum perm = PermissoesEnum.values()[i];
            PermissionDTO permission = permissionService.findBySerial(perm.getSerial());

            if (Objects.isNull(permission)) {
                permission = new PermissionDTO();
                permission.setId(perm.getId());
            }

            permission.setPermission(perm.getPermission());
            permission.setPermissionDescription(perm.getPermissionDescription());
            permission.setScreen(perm.getScreen());
            permission.setSerial(perm.getSerial());
            permissionService.save(permission);
        }
    }
}