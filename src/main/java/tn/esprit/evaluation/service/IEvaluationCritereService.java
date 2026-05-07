package tn.esprit.evaluation.service;

import tn.esprit.evaluation.entite.EvaluationCritere;
import java.util.List;

public interface IEvaluationCritereService {
    // CRUD de base
    List<EvaluationCritere> retrieveAllCriteres();
    EvaluationCritere retrieveCritere(Long critereId);
    EvaluationCritere addCritere(EvaluationCritere critere, Long evaluationId);
    void removeCritere(Long critereId);
    EvaluationCritere modifyCritere(EvaluationCritere critere);

    // Méthodes spécifiques
    List<EvaluationCritere> retrieveCriteresByEvaluation(Long evaluationId);
    Double getAverageNoteByEvaluation(Long evaluationId);
}