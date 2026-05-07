package tn.esprit.evaluation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.evaluation.entite.ReputationScore;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReputationScoreRepository extends JpaRepository<ReputationScore, Long> {

    Optional<ReputationScore> findByFreelancerId(Long freelancerId);

    List<ReputationScore> findAllByOrderByScoreGlobalDesc();
}
