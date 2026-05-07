package tn.esprit.evaluation.service;

import tn.esprit.evaluation.entite.Evaluation;
import tn.esprit.evaluation.entite.TypeEvaluation;
import tn.esprit.evaluation.entite.StatusEvaluation;
import java.util.List;

public interface IEvaluationService {
    // CRUD de base
    List<Evaluation> retrieveAllEvaluations();
    Evaluation retrieveEvaluation(Long evaluationId);
    Evaluation addEvaluation(Evaluation evaluation);
    void removeEvaluation(Long evaluationId);
    Evaluation modifyEvaluation(Evaluation evaluation);

    // Méthodes spécifiques
    List<Evaluation> retrieveEvaluationsByEvalue(Long evalueId);
    List<Evaluation> retrieveEvaluationsByEvaluateur(Long evaluateurId);
    List<Evaluation> retrieveEvaluationsByMission(Long missionId);
    List<Evaluation> retrieveEvaluationsByType(TypeEvaluation type);
    List<Evaluation> retrieveEvaluationsByStatus(StatusEvaluation status);
    Double getAverageNoteForEvalue(Long evalueId);
    Long getEvaluationsCountForEvalue(Long evalueId);
    boolean hasEvaluatedMission(Long missionId, Long evaluateurId);
}