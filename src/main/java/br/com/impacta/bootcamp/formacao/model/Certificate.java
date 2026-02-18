package br.com.impacta.bootcamp.formacao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID token;

    private String userName;

    @Temporal(TemporalType.DATE)
    private Date data;
}
