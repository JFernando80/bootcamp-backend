package br.com.impacta.bootcamp.formacao.model;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.commons.util.Ignore;
import br.com.impacta.bootcamp.commons.util.Unico;
import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.formacao.enums.StatusCourse;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="courses_sequence")
    @SequenceGenerator(name="courses_sequence", sequenceName="courses_seq", allocationSize = 0)
    private UUID id; // uuid PRIMARY KEY DEFAULT uuid_generate_v4()

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // text NOT NULL UNIQUE

    @Unico
    @Validation(required = true, lengthMax = 100, lengthMin = 10)
    @Column(name = "title", nullable = false, length = 100)
    private String title; // text NOT NULL

    @Validation(required = true, lengthMax = 300, lengthMin = 10)
    @Column(name = "description", length = 300)
    private String description; // text

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", referencedColumnName = "id")
    private User ownerUser; // owner_user_id uuid REFERENCES users(id)

    @Column(name = "status")
    private String status; // text DEFAULT 'draft'

    @Column(name = "published_at")
    private Date publishedAt; // timestamptz

    @Ignore
    @Column(name = "created_at", updatable = false)
    private Date createdAt; // timestamptz DEFAULT now()

    @Ignore
    @Column(name = "updated_at")
    private Date updatedAt; // timestamptz DEFAULT now()

}
