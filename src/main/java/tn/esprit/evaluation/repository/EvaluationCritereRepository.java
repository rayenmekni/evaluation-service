package tn.esprit.evaluation.repository;

import tn.esprit.evaluation.entite.EvaluationCritere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluationCritereRepository extends JpaRepository<EvaluationCritere, Long> {

    // Trouver tous les critères d'une évaluation spécifique
    List<EvaluationCritere> findByEvaluationIdEvaluation(Long evaluationId);

    // Calculer la note moyenne des critères pour une évaluation
    @Query("SELECT AVG(ec.note) FROM EvaluationCritere ec WHERE ec.evaluation.idEvaluation = :evaluationId")
    Double findAverageNoteByEvaluationId(@Param("evaluationId") Long evaluationId);

    // Supprimer tous les critères d'une évaluation
    void deleteByEvaluationIdEvaluation(Long evaluationId);
}