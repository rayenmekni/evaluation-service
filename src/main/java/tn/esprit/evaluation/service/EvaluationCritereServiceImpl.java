package tn.esprit.evaluation.service;

import lombok.AllArgsConstructor;
import tn.esprit.evaluation.entite.Evaluation;
import tn.esprit.evaluation.entite.EvaluationCritere;
import tn.esprit.evaluation.repository.EvaluationCritereRepository;
import tn.esprit.evaluation.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class EvaluationCritereServiceImpl implements IEvaluationCritereService {

    private final EvaluationCritereRepository evaluationCritereRepository;
    private final EvaluationRepository evaluationRepository;
    private final IEvaluationService evaluationService;

    @Override
    public List<EvaluationCritere> retrieveAllCriteres() {
        return evaluationCritereRepository.findAll();
    }

    @Override
    public EvaluationCritere retrieveCritere(Long critereId) {
        return evaluationCritereRepository.findById(critereId)
                .orElseThrow(() -> new RuntimeException("Critère non trouvé avec l'id: " + critereId));
    }

    @Override
    public EvaluationCritere addCritere(EvaluationCritere critere, Long evaluationId) {
        Evaluation evaluation = evaluationService.retrieveEvaluation(evaluationId);
        critere.setEvaluation(evaluation);

        // Validation de la note (1-5)
        if (critere.getNote() < 1 || critere.getNote() > 5) {
            throw new RuntimeException("La note du critère doit être entre 1 et 5");
        }

        return evaluationCritereRepository.save(critere);
    }

    @Override
    public void removeCritere(Long critereId) {
        EvaluationCritere critere = retrieveCritere(critereId);
        evaluationCritereRepository.delete(critere);
    }

    @Override
    public EvaluationCritere modifyCritere(EvaluationCritere critere) {
        retrieveCritere(critere.getIdCritere());

        // Validation de la note
        if (critere.getNote() < 1 || critere.getNote() > 5) {
            throw new RuntimeException("La note du critère doit être entre 1 et 5");
        }

        return evaluationCritereRepository.save(critere);
    }

    @Override
    public List<EvaluationCritere> retrieveCriteresByEvaluation(Long evaluationId) {
        return evaluationCritereRepository.findByEvaluationIdEvaluation(evaluationId);
    }

    @Override
    public Double getAverageNoteByEvaluation(Long evaluationId) {
        return evaluationCritereRepository.findAverageNoteByEvaluationId(evaluationId);
    }
}