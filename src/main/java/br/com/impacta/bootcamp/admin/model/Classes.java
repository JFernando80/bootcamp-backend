package br.com.impacta.bootcamp.admin.model;

import br.com.impacta.bootcamp.commons.util.Validation;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "classes")
@Data
public class Classes {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="classes_sequence")
    @SequenceGenerator(name="classes_sequence", sequenceName="classes_seq", allocationSize = 0)
    private long id;

    @Validation(required = true, lengthMin = 10, lengthMax = 500)
    @Column(name = "name", length = 500)
    private String name;

    @Validation(required = true, lengthMin = 10, lengthMax = 100)
    @Column(name = "simple_name", length = 100)
    private String simpleName;

}
