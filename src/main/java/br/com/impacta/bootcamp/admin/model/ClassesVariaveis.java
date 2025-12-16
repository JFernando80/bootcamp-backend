package br.com.impacta.bootcamp.admin.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "classes_variaveis")
@Data
public class ClassesVariaveis {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="classes_variaveis_sequence")
    @SequenceGenerator(name="classes_variaveis_sequence", sequenceName="classes_variaveis_seq", allocationSize = 0)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "classes")
    private Classes classes;

    @Column(name = "variavel", length = 70)
    private String variavel;

    @Column(name = "tipo", length = 70)
    private String tipo;

    @Column(name = "status", length = 50)
    private String status;

}
