package br.com.impacta.bootcamp.admin.model;

import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import br.com.impacta.bootcamp.commons.util.Unico;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_permission_type")
@Data
@EqualsAndHashCode
public class UserPermissionType {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="user_permission_sequence")
    @SequenceGenerator(name="user_permission_sequence", sequenceName="user_permission_seq", allocationSize = 0)
    private Long id;

    @Unico
    @ClassePersonal(busca = "id", exibe = "name")
    @ManyToOne
    @JoinColumn(name = "user_a", nullable = false)
    private User user;

    @Unico
    @ClassePersonal(busca = "id", exibe = "descricao")
    @ManyToOne
    @JoinColumn(name = "permission_type", nullable = false)
    private PermissionType permissionType;

}
