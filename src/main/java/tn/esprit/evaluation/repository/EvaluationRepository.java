package tn.esprit.evaluation.repository;

import tn.esprit.evaluation.entite.Evaluation;
import tn.esprit.evaluation.entite.StatusEvaluation;
import tn.esprit.evaluation.entite.TypeEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByEvalueId(Long evalueId);

    List<Evaluation> findByEvaluateurId(Long evaluateurId);

    List<Evaluation> findByMissionId(Long missionId);

    List<Evaluation> findByTypeEvaluation(TypeEvaluation typeEvaluation);

    List<Evaluation> findByStatus(StatusEvaluation status);

    @Query("SELECT AVG(e.noteGlobal) FROM Evaluation e WHERE e.evalueId = :evalueId")
    Double findAverageNoteByEvalueId(@Param("evalueId") Long evalueId);

    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.evalueId = :evalueId")
    Long countByEvalueId(@Param("evalueId") Long evalueId);

    boolean existsByMissionIdAndEvaluateurId(Long missionId, Long evaluateurId);
}