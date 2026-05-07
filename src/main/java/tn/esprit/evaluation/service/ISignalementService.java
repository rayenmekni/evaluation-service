package tn.esprit.evaluation.service;

import tn.esprit.evaluation.entite.ModerationHistory;
import tn.esprit.evaluation.entite.ModerationStatus;
import tn.esprit.evaluation.entite.Signalement;

import java.util.List;

public interface ISignalementService {

    Signalement signalerEvaluation(Long evaluationId, String motif, Long reportedBy);

    List<Signalement> getSignalementsByStatus(ModerationStatus status);

    List<Signalement> getSignalementsByEvaluation(Long evaluationId);

    Signalement mettreEnRevision(Long signalementId, String moderatorNote);

    Signalement validerEvaluation(Long signalementId);

    Signalement supprimerEvaluation(Long signalementId);

    List<ModerationHistory> getHistoriqueModeration(Long evaluationId);
}
