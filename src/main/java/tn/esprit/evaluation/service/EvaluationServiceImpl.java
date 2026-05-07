package tn.esprit.evaluation.service;

import lombok.AllArgsConstructor;
import tn.esprit.evaluation.entite.Evaluation;
import tn.esprit.evaluation.entite.TypeEvaluation;
import tn.esprit.evaluation.entite.StatusEvaluation;
import tn.esprit.evaluation.exception.EvaluationAlreadyExistsException;
import tn.esprit.evaluation.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class EvaluationServiceImpl implements IEvaluationService {

    private final EvaluationRepository evaluationRepository;

    @Override
    public List<Evaluation> retrieveAllEvaluations() {
        return evaluationRepository.findAll();
    }

    @Override
    public Evaluation retrieveEvaluation(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée avec l'id: " + evaluationId));
    }

    @Override
    @Transactional
    public Evaluation addEvaluation(Evaluation evaluation) {
        // Validation des notes (1-5)
        if (evaluation.getNoteQualite() < 1 || evaluation.getNoteQualite() > 5 ||
                evaluation.getNoteDelais() < 1 || evaluation.getNoteDelais() > 5 ||
                evaluation.getNoteCommunication() < 1 || evaluation.getNoteCommunication() > 5) {
            throw new RuntimeException("Les notes doivent être entre 1 et 5");
        }

        // Vérifier doublon d'évaluation
        boolean alreadyEvaluated = false;
        try {
            alreadyEvaluated = evaluationRepository.existsByMissionIdAndEvaluateurId(
                    evaluation.getMissionId(),
                    evaluation.getEvaluateurId()
            );
        } catch (Exception ex) {
            // Ne pas bloquer la création si la vérification de doublon échoue.
            alreadyEvaluated = false;
        }

        if (alreadyEvaluated) {
            throw new EvaluationAlreadyExistsException(
                    evaluation.getMissionId(),
                    evaluation.getEvaluateurId()
            );
        }

        // Calculer la note globale
        double noteGlobal = (evaluation.getNoteQualite() +
                evaluation.getNoteDelais() +
                evaluation.getNoteCommunication()) / 3.0;
        evaluation.setNoteGlobal(Math.round(noteGlobal * 10) / 10.0);

        // Définir la date d'évaluation
        if (evaluation.getDateEvaluation() == null) {
            evaluation.setDateEvaluation(LocalDate.now());
        }

        // Définir le statut par défaut
        if (evaluation.getStatus() == null) {
            evaluation.setStatus(StatusEvaluation.PUBLIEE);
        }

        return evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void removeEvaluation(Long evaluationId) {
        Evaluation evaluation = retrieveEvaluation(evaluationId);
        evaluationRepository.delete(evaluation);
    }

    @Override
    @Transactional
    public Evaluation modifyEvaluation(Evaluation evaluation) {
        retrieveEvaluation(evaluation.getIdEvaluation());

        // Validation des notes
        if (evaluation.getNoteQualite() < 1 || evaluation.getNoteQualite() > 5 ||
                evaluation.getNoteDelais() < 1 || evaluation.getNoteDelais() > 5 ||
                evaluation.getNoteCommunication() < 1 || evaluation.getNoteCommunication() > 5) {
            throw new RuntimeException("Les notes doivent être entre 1 et 5");
        }

        // Recalculer la note globale
        double noteGlobal = (evaluation.getNoteQualite() +
                evaluation.getNoteDelais() +
                evaluation.getNoteCommunication()) / 3.0;
        evaluation.setNoteGlobal(Math.round(noteGlobal * 10) / 10.0);

        // Mettre à jour le statut
        evaluation.setStatus(StatusEvaluation.MODIFIEE);

        return evaluationRepository.save(evaluation);
    }

    @Override
    public List<Evaluation> retrieveEvaluationsByEvalue(Long evalueId) {
        return evaluationRepository.findByEvalueId(evalueId);
    }

    @Override
    public List<Evaluation> retrieveEvaluationsByEvaluateur(Long evaluateurId) {
        return evaluationRepository.findByEvaluateurId(evaluateurId);
    }

    @Override
    public List<Evaluation> retrieveEvaluationsByMission(Long missionId) {
        return evaluationRepository.findByMissionId(missionId);
    }

    @Override
    public List<Evaluation> retrieveEvaluationsByType(TypeEvaluation type) {
        return evaluationRepository.findByTypeEvaluation(type);
    }

    @Override
    public List<Evaluation> retrieveEvaluationsByStatus(StatusEvaluation status) {
        return evaluationRepository.findByStatus(status);
    }

    @Override
    public Double getAverageNoteForEvalue(Long evalueId) {
        return evaluationRepository.findAverageNoteByEvalueId(evalueId);
    }

    @Override
    public Long getEvaluationsCountForEvalue(Long evalueId) {
        return evaluationRepository.countByEvalueId(evalueId);
    }

    @Override
    public boolean hasEvaluatedMission(Long missionId, Long evaluateurId) {
        return evaluationRepository.existsByMissionIdAndEvaluateurId(missionId, evaluateurId);
    }
}