package tn.esprit.evaluation.entite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "evaluation_critere")
public class EvaluationCritere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCritere;

    private String nomCritere;
    private int note;
    private String commentaire;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;
}