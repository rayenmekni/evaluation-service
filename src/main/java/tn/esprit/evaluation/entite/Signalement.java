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
@Table(name = "signalement")
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSignalement;

    private Long evaluationId;

    @Column(columnDefinition = "TEXT")
    private String motif;

    private LocalDate dateSignalement;

    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private ModerationStatus status;

    private Long reportedBy;

    @Column(columnDefinition = "TEXT")
    private String moderatorNote;

    private LocalDate dateModeration;

    @PrePersist
    public void onCreate() {
        if (dateSignalement == null) {
            dateSignalement = LocalDate.now();
        }
        if (status == null) {
            status = ModerationStatus.SIGNALEE;
        }
    }
}
