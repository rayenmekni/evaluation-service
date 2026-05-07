package tn.esprit.evaluation.entite;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "evaluation")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluation;

    private Long missionId;
    private Long evaluateurId;
    private Long evalueId;

    @Enumerated(EnumType.STRING)
    private TypeEvaluation typeEvaluation;

    private int noteQualite;
    private int noteDelais;
    private int noteCommunication;

    private double noteGlobal;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private LocalDate dateEvaluation;

    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private StatusEvaluation status;

    @JsonManagedReference
    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationCritere> criteres = new ArrayList<>();
}