package br.com.impacta.bootcamp.admin.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Table(name = "token_a")
@Data
@EqualsAndHashCode
public class Token {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="token_sequence")
    @SequenceGenerator(name="token_sequence", sequenceName="token_seq", allocationSize = 0)
    private long id;

    @Column(name = "token", length = 150)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expira_em")
    private Date expiraEm;

    @Column(name = "refresh_token", length = 150)
    private String refreshToken;

    @OneToOne()
    @JoinColumn(name = "user_a", referencedColumnName = "id")
    private User user;
}
