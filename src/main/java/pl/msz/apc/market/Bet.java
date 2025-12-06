package pl.msz.apc.market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.msz.apc.agents.Persona;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Enumerated(EnumType.STRING)
    private Persona agentPersona;

    private Double probability; // 0.0 to 1.0

    @Column(columnDefinition = "TEXT")
    private String rationale;

    private int round; // 1 = initial, 2 = after debate

    private LocalDateTime placedAt;

    @PrePersist
    void onCreate() {
        this.placedAt = LocalDateTime.now();
    }
}
