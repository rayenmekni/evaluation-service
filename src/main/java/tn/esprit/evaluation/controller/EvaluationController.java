package tn.esprit.evaluation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.evaluation.entite.Evaluation;
import tn.esprit.evaluation.service.IEvaluationService;
import java.util.List;

@RestController
@RequestMapping("/api/competence/evaluations")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EvaluationController {

    private final IEvaluationService evaluationService;

    @GetMapping("/retrieve-all-evaluations")
    public List<Evaluation> retrieveAllEvaluations() {
        return evaluationService.retrieveAllEvaluations();
    }

    @GetMapping("/retrieve-evaluation/{id}")
    public Evaluation retrieveEvaluation(@PathVariable Long id) {
        return evaluationService.retrieveEvaluation(id);
    }

    @PostMapping("/add-evaluation")
    public Evaluation addEvaluation(@RequestBody Evaluation evaluation) {
        return evaluationService.addEvaluation(evaluation);
    }

    @PutMapping("/modify-evaluation")
    public Evaluation modifyEvaluation(@RequestBody Evaluation evaluation) {
        return evaluationService.modifyEvaluation(evaluation);
    }

    @DeleteMapping("/remove-evaluation/{id}")
    public void removeEvaluation(@PathVariable Long id) {
        evaluationService.removeEvaluation(id);
    }
}