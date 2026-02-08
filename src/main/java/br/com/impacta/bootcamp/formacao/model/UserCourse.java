package br.com.impacta.bootcamp.formacao.model;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import br.com.impacta.bootcamp.commons.util.Unico;
import br.com.impacta.bootcamp.commons.util.Validation;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_courses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "course_id"}) // UNIQUE(user_id, course_id)
})
public class UserCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // uuid PRIMARY KEY DEFAULT uuid_generate_v4()

    @Validation(dateMax = "+0d")
    @Column(name = "enrolled_at")
    private Date enrolledAt; // timestamptz DEFAULT now()

    @Column(name = "status", length = 20)
    private String status; // text DEFAULT 'enrolled'

    @Validation(required = true)
    @Column(name = "progress_percent")
    private Long progressPercent; // int DEFAULT 0

    @Column(name = "certificate_issued_at")
    private Date certificateIssuedAt; // timestamptz

    @Column(name = "certificate_token", unique = true)
    private String certificateToken; // text UNIQUE

    @Column(name = "certificate_url")
    private String certificateUrl; // text

    @Validation(dateMax = "+0d")
    @Column(name = "last_activity_at")
    private Date lastActivityAt; // timestamptz

    @Unico
    @ClassePersonal(busca = "id", exibe = "name")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // user_id uuid REFERENCES users(id)

    @Unico
    @ClassePersonal(busca = "id", exibe = "description")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course; // course_id uuid REFERENCES courses(id)
}
