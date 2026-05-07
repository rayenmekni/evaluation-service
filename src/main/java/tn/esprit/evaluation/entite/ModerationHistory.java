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
@Table(name = "moderation_history")
public class ModerationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistory;

    private Long evaluationId;

    private String action;

    private Long performedBy;

    @Column(columnDefinition = "TEXT")
    private String note;

    private LocalDate dateAction;

    @PrePersist
    public void onCreate() {
        if (dateAction == null) {
            dateAction = LocalDate.now();
        }
    }
}
