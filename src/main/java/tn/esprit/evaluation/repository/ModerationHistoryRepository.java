package tn.esprit.evaluation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.evaluation.entite.ModerationHistory;

import java.util.List;

@Repository
public interface ModerationHistoryRepository extends JpaRepository<ModerationHistory, Long> {

    List<ModerationHistory> findByEvaluationId(Long evaluationId);
}
