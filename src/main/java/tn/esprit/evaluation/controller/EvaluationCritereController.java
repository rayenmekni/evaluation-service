package tn.esprit.evaluation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.evaluation.entite.EvaluationCritere;
import tn.esprit.evaluation.service.IEvaluationCritereService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-criteres")
@AllArgsConstructor
public class EvaluationCritereController {

    private final IEvaluationCritereService evaluationCritereService;

    // http://localhost:8089/Competence/api/evaluation-criteres/retrieve-all-criteres
    @GetMapping("/retrieve-all-criteres")
    public List<EvaluationCritere> retrieveAllCriteres() {
        return evaluationCritereService.retrieveAllCriteres();
    }

    // http://localhost:8089/Competence/api/evaluation-criteres/retrieve-critere/{id}
    @GetMapping("/retrieve-critere/{id}")
    public EvaluationCritere retrieveCritere(@PathVariable("id") Long critereId) {
        return evaluationCritereService.retrieveCritere(critereId);
    }

    // http://localhost:8089/Competence/api/evaluation-criteres/add-critere/{evaluationId}
    @PostMapping("/add-critere/{evaluationId}")
    public EvaluationCritere addCritere(@RequestBody EvaluationCritere critere,
                                        @PathVariable Long evaluationId) {
        return evaluationCritereService.addCritere(critere, evaluationId);
    }

    // http://localhost:8089/Competence/api/evaluation-criteres/remove-critere/{id}
    @DeleteMapping("/remove-critere/{id}")
    public void removeCritere(@PathVariable("id") Long critereId) {
        evaluationCritereService.removeCritere(critereId);
    }

    // http://localhost:8089/Competence/api/evaluation-criteres/modify-critere
    @PutMapping("/modify-critere")
    public EvaluationCritere modifyCritere(@RequestBody EvaluationCritere critere) {
        return evaluationCritereService.modifyCritere(critere);
    }

    // http://localhost:8089/Competence/api/evaluation-criteres/evaluation/{evaluationId}
    @GetMapping("/evaluation/{evaluationId}")
    public List<EvaluationCritere> retrieveCriteresByEvaluation(@PathVariable Long evaluationId) {
        return evaluationCritereService.retrieveCriteresByEvaluation(evaluationId);
    }

    // http://localhost:8089/Competence/api/evaluation-criteres/average/{evaluationId}
    @GetMapping("/average/{evaluationId}")
    public Double getAverageNoteByEvaluation(@PathVariable Long evaluationId) {
        return evaluationCritereService.getAverageNoteByEvaluation(evaluationId);
    }
}