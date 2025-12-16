package br.com.impacta.bootcamp.seguranca.model;

import br.com.impacta.bootcamp.admin.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "security")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Security implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="security_sequence")
    @SequenceGenerator(name="security_sequence", sequenceName="security_seq")
    private Long id;
    private String publicKey;
    private String privateKey;
    private String screen;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Date expirationDate;

    public static Security buildSecurity(String publicKey, String privateKey,
                                         String screen, Date expirationDate) {
        Security security = new Security();
        security.expirationDate = expirationDate;
        security.privateKey = privateKey;
        security.publicKey = publicKey;
        security.screen = screen;
        return security;
    }
}
