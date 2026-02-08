package br.com.impacta.bootcamp.formacao.model;

import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import br.com.impacta.bootcamp.commons.util.Ignore;
import br.com.impacta.bootcamp.commons.util.Unico;
import br.com.impacta.bootcamp.commons.util.Validation;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "modules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "index"}) // UNIQUE(course_id, "index")
})
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // uuid PRIMARY KEY DEFAULT uuid_generate_v4()

    @Validation(required = true, lengthMin = 1, lengthMax = 100)
    @Column(name = "index", nullable = false)
    private Long index; // "index" int NOT NULL

    @Unico
    @Validation(required = true, lengthMin = 10, lengthMax = 100)
    @Column(name = "title", length = 100)
    private String title; // text

    @Validation(required = true, lengthMin = 10, lengthMax = 300)
    @Column(name = "description", length = 300)
    private String description; // text

    @Column(name = "required_to_complete_course")
    private Boolean requiredToCompleteCourse; // boolean DEFAULT true

    @Validation(dateMax = "+0d")
    @Column(name = "created_at", updatable = false)
    private Date createdAt; // timestamptz DEFAULT now()

    @Ignore
    @Validation(dateMax = "+0d")
    @Column(name = "updated_at")
    private Date updatedAt; // timestamptz DEFAULT now()

    @Unico
    @ClassePersonal(busca = "id", exibe = "description")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course; // course_id uuid REFERENCES courses(id) ON DELETE CASCADE
}
