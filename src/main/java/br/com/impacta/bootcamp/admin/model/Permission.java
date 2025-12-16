package br.com.impacta.bootcamp.admin.model;

import br.com.impacta.bootcamp.commons.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "permission_a")
@Data
@EqualsAndHashCode
public class Permission {

    @Id
    private long id;

    @Column(name = "permission", length = 50, nullable = false)
    private String permission;

    @Column(name = "permission_description", length = 150, nullable = false)
    private String permissionDescription;

    @Column(name = "screen", length = 80, nullable = false)
    private String screen;

    @Column(name = "status")
    private Status status;

    @Column(name = "serial")
    private long serial;

}
