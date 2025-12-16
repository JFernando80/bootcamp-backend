package br.com.impacta.bootcamp.admin.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "classes_variaveis_locale")
@Data
public class ClassesVariaveisLocale {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="classes_variaveis_locale_sequence")
    @SequenceGenerator(name="classes_variaveis_locale_sequence", sequenceName="classes_variaveis_locale_seq", allocationSize = 0)
    private Long id;

    @Column(name = "locale", length = 10)
    private String locale;

    @OneToOne()
    @JoinColumn(name = "classes")
    private ClassesVariaveis classesVariaveis;

    @Column(name = "descricao", length = 100)
    private String descricao;
}
