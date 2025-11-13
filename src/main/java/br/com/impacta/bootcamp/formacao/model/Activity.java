package br.com.impacta.bootcamp.formacao.model;

import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import br.com.impacta.bootcamp.commons.util.Unico;
import br.com.impacta.bootcamp.commons.util.Validation;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activities_sequence")
    @SequenceGenerator(name = "activities_sequence", sequenceName = "activities_seq", allocationSize = 0)
    private UUID id; // uuid PRIMARY KEY DEFAULT uuid_generate_v4()

    @Unico
    @Validation(required = true, lengthMax = 100, lengthMin = 5)
    @Column(name = "type")
    private String type; // text

    @Validation(required = true)
    @Column(name = "config")
    private String configJson; // ou Map<String, Object> config;

    @Validation(required = true)
    @Column(name = "max_score")
    private Long maxScore; // int DEFAULT 100

    @Validation(required = true)
    @Column(name = "passing_score")
    private Long passingScore; // int DEFAULT 70

    @Column(name = "created_at", updatable = false)
    private Date createdAt; // timestamptz DEFAULT now()

    @Column(name = "updated_at")
    private Date updatedAt; // timestamptz DEFAULT now()

    @Unico
    @ClassePersonal(busca = "id", exibe = "description")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id", nullable = false)
    private Module module; // module_id uuid REFERENCES modules(id) ON DELETE CASCADE
}