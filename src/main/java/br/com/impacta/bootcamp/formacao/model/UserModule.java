package br.com.impacta.bootcamp.formacao.model;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import br.com.impacta.bootcamp.commons.util.Unico;
import br.com.impacta.bootcamp.commons.util.Validation;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;
import java.time.Instant;

@Data
@Entity
@Table(name = "user_modules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "module_id"}) // UNIQUE(user_id, module_id)
})
public class UserModule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // uuid PRIMARY KEY DEFAULT uuid_generate_v4()

    @Validation(dateMax = "+0d")
    @Column(name = "started_at")
    private Date startedAt; // timestamptz

    @Validation(dateMax = "+0d")
    @Column(name = "completed_at")
    private Date completedAt; // timestamptz

    @Column(name = "status")
    private String status; // text DEFAULT 'not_started'

    @Column(name = "score")
    private Long score; // int

    @Unico
    @ClassePersonal(busca = "id", exibe = "name")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // user_id uuid REFERENCES users(id)

    @Unico
    @ClassePersonal(busca = "id", exibe = "title")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id", nullable = false)
    private Module module; // module_id uuid REFERENCES modules(id)
}
