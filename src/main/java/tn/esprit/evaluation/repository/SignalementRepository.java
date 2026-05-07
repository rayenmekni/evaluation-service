package tn.esprit.evaluation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.evaluation.entite.ModerationStatus;
import tn.esprit.evaluation.entite.Signalement;

import java.util.List;

@Repository
public interface SignalementRepository extends JpaRepository<Signalement, Long> {

    List<Signalement> findByEvaluationId(Long evaluationId);

    List<Signalement> findByStatus(ModerationStatus status);

    boolean existsByEvaluationIdAndReportedBy(Long evaluationId, Long reportedBy);
}
