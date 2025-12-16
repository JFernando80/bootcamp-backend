package br.com.impacta.bootcamp.admin.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "permission_perfil")
@Data
@EqualsAndHashCode
public class PerfilPermissionType {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="perfil_permissao_sequence")
    @SequenceGenerator(name="perfil_permissao_sequence", sequenceName="perfil_permissao_seq", allocationSize = 0)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "tipo_permissao", nullable = false)
    private PermissionType permissionType;

    @ManyToOne()
    @JoinColumn(name = "permissao", nullable = false)
    private Permission permission;

}
