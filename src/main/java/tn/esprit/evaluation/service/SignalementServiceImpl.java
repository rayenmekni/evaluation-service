package tn.esprit.evaluation.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.evaluation.entite.*;
import tn.esprit.evaluation.repository.EvaluationRepository;
import tn.esprit.evaluation.repository.ModerationHistoryRepository;
import tn.esprit.evaluation.repository.SignalementRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class SignalementServiceImpl implements ISignalementService {

    private final SignalementRepository signalementRepository;
    private final EvaluationRepository evaluationRepository;
    private final ModerationHistoryRepository moderationHistoryRepository;

    @Override
    @Transactional
    public Signalement signalerEvaluation(Long evaluationId, String motif, Long reportedBy) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation non trouvee avec id: " + evaluationId));

        if (signalementRepository.existsByEvaluationIdAndReportedBy(evaluationId, reportedBy)) {
            throw new RuntimeException("Cette evaluation est deja signalee par cet utilisateur");
        }

        boolean autoSignalement = evaluation.getNoteGlobal() < 2
                && (evaluation.getCommentaire() == null || evaluation.getCommentaire().trim().isEmpty());
        String finalMotif = autoSignalement
                ? "Auto-signalement: note faible sans commentaire"
                : motif;

        Signalement signalement = Signalement.builder()
                .evaluationId(evaluationId)
                .motif(finalMotif)
                .dateSignalement(LocalDate.now())
                .status(ModerationStatus.SIGNALEE)
                .reportedBy(reportedBy)
                .build();

        Signalement saved = signalementRepository.save(signalement);

        evaluation.setStatus(StatusEvaluation.SIGNALEE);
        evaluationRepository.save(evaluation);

        saveHistory(evaluationId, "SIGNALEMENT", reportedBy, finalMotif);
        return saved;
    }

    @Override
    public List<Signalement> getSignalementsByStatus(ModerationStatus status) {
        return signalementRepository.findByStatus(status);
    }

    @Override
    public List<Signalement> getSignalementsByEvaluation(Long evaluationId) {
        return signalementRepository.findByEvaluationId(evaluationId);
    }

    @Override
    @Transactional
    public Signalement mettreEnRevision(Long signalementId, String moderatorNote) {
        Signalement signalement = getSignalementOrThrow(signalementId);
        Evaluation evaluation = getEvaluationOrThrow(signalement.getEvaluationId());

        signalement.setStatus(ModerationStatus.EN_REVISION);
        signalement.setModeratorNote(moderatorNote);
        signalement.setDateModeration(LocalDate.now());
        Signalement saved = signalementRepository.save(signalement);

        evaluation.setStatus(StatusEvaluation.EN_REVISION);
        evaluationRepository.save(evaluation);

        saveHistory(evaluation.getIdEvaluation(), "MISE_EN_REVISION", 0L, moderatorNote);
        return saved;
    }

    @Override
    @Transactional
    public Signalement validerEvaluation(Long signalementId) {
        Signalement signalement = getSignalementOrThrow(signalementId);
        Evaluation evaluation = getEvaluationOrThrow(signalement.getEvaluationId());

        signalement.setStatus(ModerationStatus.VALIDEE);
        signalement.setDateModeration(LocalDate.now());
        Signalement saved = signalementRepository.save(signalement);

        evaluation.setStatus(StatusEvaluation.VALIDEE);
        evaluationRepository.save(evaluation);

        saveHistory(evaluation.getIdEvaluation(), "VALIDATION", 0L, "Evaluation validee");
        return saved;
    }

    @Override
    @Transactional
    public Signalement supprimerEvaluation(Long signalementId) {
        Signalement signalement = getSignalementOrThrow(signalementId);
        Evaluation evaluation = getEvaluationOrThrow(signalement.getEvaluationId());

        signalement.setStatus(ModerationStatus.SUPPRIMEE);
        signalement.setDateModeration(LocalDate.now());
        Signalement saved = signalementRepository.save(signalement);

        evaluation.setStatus(StatusEvaluation.SUPPRIMEE);
        evaluationRepository.save(evaluation);

        saveHistory(evaluation.getIdEvaluation(), "SUPPRESSION", 0L, "Evaluation supprimee");
        return saved;
    }

    @Override
    public List<ModerationHistory> getHistoriqueModeration(Long evaluationId) {
        return moderationHistoryRepository.findByEvaluationId(evaluationId);
    }

    private Signalement getSignalementOrThrow(Long signalementId) {
        return signalementRepository.findById(signalementId)
                .orElseThrow(() -> new RuntimeException("Signalement non trouve avec id: " + signalementId));
    }

    private Evaluation getEvaluationOrThrow(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation non trouvee avec id: " + evaluationId));
    }

    private void saveHistory(Long evaluationId, String action, Long performedBy, String note) {
        ModerationHistory history = ModerationHistory.builder()
                .evaluationId(evaluationId)
                .action(action)
                .performedBy(performedBy)
                .note(note)
                .dateAction(LocalDate.now())
                .build();
        moderationHistoryRepository.save(history);
    }
}
