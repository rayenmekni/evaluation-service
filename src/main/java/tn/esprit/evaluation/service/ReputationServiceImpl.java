package tn.esprit.evaluation.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.evaluation.entite.*;
import tn.esprit.evaluation.repository.EvaluationRepository;
import tn.esprit.evaluation.repository.ReputationScoreRepository;
import tn.esprit.evaluation.repository.SignalementRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ReputationServiceImpl implements IReputationService {

    private final EvaluationRepository evaluationRepository;
    private final ReputationScoreRepository reputationScoreRepository;
    private final SignalementRepository signalementRepository;

    @Override
    @Transactional
    public ReputationScore calculerScore(Long freelancerId) {
        List<Evaluation> evaluations = evaluationRepository.findByEvalueId(freelancerId);
        if (evaluations.isEmpty()) {
            throw new RuntimeException("Aucune evaluation trouvee pour ce freelancer");
        }

        autoSignalerEvaluationSuspecte(evaluations);

        LocalDate threshold = LocalDate.now().minusMonths(3);
        double weightedSum = 0.0;
        double totalWeight = 0.0;

        for (Evaluation evaluation : evaluations) {
            double score = evaluation.getNoteGlobal() > 0
                    ? evaluation.getNoteGlobal()
                    : (evaluation.getNoteQualite() + evaluation.getNoteDelais() + evaluation.getNoteCommunication()) / 3.0;

            double weight = (evaluation.getDateEvaluation() != null && !evaluation.getDateEvaluation().isBefore(threshold))
                    ? 1.5
                    : 1.0;
            weightedSum += score * weight;
            totalWeight += weight;
        }

        double newScore = totalWeight > 0 ? (weightedSum / totalWeight) : 0.0;
        newScore = Math.round(newScore * 100.0) / 100.0;

        ReputationScore existing = reputationScoreRepository.findByFreelancerId(freelancerId).orElse(null);
        double previousScore = existing != null ? existing.getScoreGlobal() : 0.0;
        double evolution = Math.round((newScore - previousScore) * 100.0) / 100.0;

        ReputationScore reputation = existing != null ? existing : new ReputationScore();
        reputation.setFreelancerId(freelancerId);
        reputation.setScoreGlobal(newScore);
        reputation.setNombreEvaluations(evaluations.size());
        reputation.setNiveau(NiveauReputation.fromScore(newScore));
        reputation.setDateCalcul(LocalDate.now());
        reputation.setEvolutionScore(evolution);

        return reputationScoreRepository.save(reputation);
    }

    @Override
    public List<ReputationScore> getClassement() {
        return reputationScoreRepository.findAllByOrderByScoreGlobalDesc();
    }

    @Override
    public ReputationScore getScoreByFreelancer(Long freelancerId) {
        return reputationScoreRepository.findByFreelancerId(freelancerId)
                .orElseThrow(() -> new RuntimeException("Score de reputation non trouve pour freelancer: " + freelancerId));
    }

    private void autoSignalerEvaluationSuspecte(List<Evaluation> evaluations) {
        long oneStarCount = evaluations.stream()
                .filter(e -> getScore(e) <= 1.5)
                .count();
        boolean othersHigh = evaluations.stream()
                .filter(e -> getScore(e) > 1.5)
                .allMatch(e -> getScore(e) >= 4.0);

        if (oneStarCount == 1 && othersHigh) {
            Evaluation suspicious = evaluations.stream()
                    .filter(e -> getScore(e) <= 1.5)
                    .findFirst()
                    .orElse(null);
            if (suspicious == null || suspicious.getIdEvaluation() == null) {
                return;
            }

            Long evaluationId = suspicious.getIdEvaluation();
            Long systemReporterId = 0L;
            if (!signalementRepository.existsByEvaluationIdAndReportedBy(evaluationId, systemReporterId)) {
                Signalement signalement = Signalement.builder()
                        .evaluationId(evaluationId)
                        .motif("Auto-signalement: evaluation suspecte (1 etoile isolee)")
                        .status(ModerationStatus.SIGNALEE)
                        .reportedBy(systemReporterId)
                        .dateSignalement(LocalDate.now())
                        .build();
                signalementRepository.save(signalement);

                suspicious.setStatus(StatusEvaluation.SIGNALEE);
                evaluationRepository.save(suspicious);
            }
        }
    }

    private double getScore(Evaluation evaluation) {
        return evaluation.getNoteGlobal() > 0
                ? evaluation.getNoteGlobal()
                : (evaluation.getNoteQualite() + evaluation.getNoteDelais() + evaluation.getNoteCommunication()) / 3.0;
    }
}
