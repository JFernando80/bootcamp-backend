package br.com.impacta.bootcamp.admin.model;

import br.com.impacta.bootcamp.commons.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "grupo_permissao")
@Data
@EqualsAndHashCode
public class PermissionGroup {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="grupo_permissao_sequence")
    @SequenceGenerator(name="grupo_permissao_sequence", sequenceName="grupo_permissao_seq", allocationSize = 0)
    private long id;

    @Column(name = "descricao", length = 50)
    private String descricao;

    @Column(name = "status")
    private Status status;

}
