package br.com.impacta.bootcamp.admin.model;

import br.com.impacta.bootcamp.commons.enums.Status;
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

    @Column(name = "password_hash", length = 150, nullable = false)
    private String passwordHash;

    @Column(name = "salt", length = 150, nullable = false)
    private String salt;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

}
