package tn.esprit.evaluation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.evaluation.entite.ReputationScore;
import tn.esprit.evaluation.service.IReputationService;

import java.util.List;

@RestController
@RequestMapping("/api/reputation")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ReputationController {

    private final IReputationService reputationService;

    @GetMapping("/freelancer/{freelancerId}")
    public ReputationScore getScoreByFreelancer(@PathVariable Long freelancerId) {
        return reputationService.getScoreByFreelancer(freelancerId);
    }

    @PostMapping("/calculer/{freelancerId}")
    public ReputationScore calculerScore(@PathVariable Long freelancerId) {
        return reputationService.calculerScore(freelancerId);
    }

    @GetMapping("/classement")
    public List<ReputationScore> getClassement() {
        return reputationService.getClassement();
    }
}
