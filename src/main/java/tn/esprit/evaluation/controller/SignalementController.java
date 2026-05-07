package tn.esprit.evaluation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.evaluation.entite.ModerationHistory;
import tn.esprit.evaluation.entite.ModerationStatus;
import tn.esprit.evaluation.entite.Signalement;
import tn.esprit.evaluation.service.ISignalementService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/signalements")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class SignalementController {

    private final ISignalementService signalementService;

    @PostMapping("/signaler/{evaluationId}")
    public Signalement signalerEvaluation(
            @PathVariable Long evaluationId,
            @RequestBody Map<String, Object> payload
    ) {
        String motif = payload.get("motif") != null ? payload.get("motif").toString() : "";
        Long reportedBy = payload.get("reportedBy") != null
                ? Long.valueOf(payload.get("reportedBy").toString())
                : 0L;

        return signalementService.signalerEvaluation(evaluationId, motif, reportedBy);
    }

    @GetMapping("/status/{status}")
    public List<Signalement> getSignalementsByStatus(@PathVariable ModerationStatus status) {
        return signalementService.getSignalementsByStatus(status);
    }

    @GetMapping("/evaluation/{evaluationId}")
    public List<Signalement> getSignalementsByEvaluation(@PathVariable Long evaluationId) {
        return signalementService.getSignalementsByEvaluation(evaluationId);
    }

    @PutMapping("/revision/{signalementId}")
    public Signalement mettreEnRevision(
            @PathVariable Long signalementId,
            @RequestBody Map<String, Object> payload
    ) {
        String moderatorNote = payload.get("moderatorNote") != null
                ? payload.get("moderatorNote").toString()
                : "";
        return signalementService.mettreEnRevision(signalementId, moderatorNote);
    }

    @PutMapping("/valider/{signalementId}")
    public Signalement validerEvaluation(@PathVariable Long signalementId) {
        return signalementService.validerEvaluation(signalementId);
    }

    @PutMapping("/supprimer/{signalementId}")
    public Signalement supprimerEvaluation(@PathVariable Long signalementId) {
        return signalementService.supprimerEvaluation(signalementId);
    }

    @GetMapping("/historique/{evaluationId}")
    public List<ModerationHistory> getHistoriqueModeration(@PathVariable Long evaluationId) {
        return signalementService.getHistoriqueModeration(evaluationId);
    }
}
