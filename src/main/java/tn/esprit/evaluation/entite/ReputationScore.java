package tn.esprit.evaluation.entite;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "reputation_score")
public class ReputationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReputation;

    private Long freelancerId;

    private double scoreGlobal;

    private int nombreEvaluations;

    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private NiveauReputation niveau;

    private LocalDate dateCalcul;

    private double evolutionScore;

    @PrePersist
    @PreUpdate
    public void preSave() {
        if (dateCalcul == null) {
            dateCalcul = LocalDate.now();
        }
    }
}
