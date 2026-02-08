package br.com.impacta.bootcamp.formacao.model;

import br.com.impacta.bootcamp.admin.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_activities")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // uuid PRIMARY KEY DEFAULT uuid_generate_v4()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // user_id uuid REFERENCES users(id)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", referencedColumnName = "id", nullable = false)
    private Activity activity; // activity_id uuid REFERENCES activities(id)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id", nullable = false)
    private Module module; // module_id uuid REFERENCES modules(id)

    @Column(name = "attempt_number")
    private Long attemptNumber; // int DEFAULT 1

    // jsonb - Mapeamento similar ao UserProfile
    @Column(name = "answer_json")
    private String answerJson; // ou Map<String, Object> answerJson;

    @Column(name = "score")
    private Long score; // int

    @Column(name = "submitted_at")
    private Date submittedAt; // timestamptz DEFAULT now()

    @Column(name = "status")
    private String status; // text

}
