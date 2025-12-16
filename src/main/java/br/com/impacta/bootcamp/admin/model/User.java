package br.com.impacta.bootcamp.admin.model;

import br.com.impacta.bootcamp.commons.util.Ignore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 120)
    private String name;

    @Column(unique = true, length = 100)
    private String email;

    @Column(name = "sobrenome", length = 100)
    private String sobrenome;

    @Ignore
    private Boolean administrador;

    @Ignore
    @Column(name = "password_hash", length = 150, nullable = false)
    private String passwordHash;

    @Ignore
    @Column(name = "salt", length = 150, nullable = false)
    private String salt;

    @Ignore
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Ignore
    @Column(name = "updated_at")
    private Date updatedAt;

    @Ignore
    @Column(name = "deleted_at")
    private Date deletedAt;

}
