package br.com.impacta.bootcamp.admin.model;

import br.com.impacta.bootcamp.commons.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tipo_permissao")
@Data
@EqualsAndHashCode
public class PermissionType {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="tipo_permissao_sequence")
    @SequenceGenerator(name="tipo_permissao_sequence", sequenceName="tipo_permissao_seq", allocationSize = 0)
    private long id;

    @Column(name = "descricao", length = 50)
    private String descricao;

    @Column(name = "status")
    private Status status;

    @ManyToOne()
    @JoinColumn(name = "grupo_permissao", nullable = false)
    private PermissionGroup permissionGroup;

}
