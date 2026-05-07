package tn.esprit.evaluation.service;

import tn.esprit.evaluation.entite.ReputationScore;

import java.util.List;

public interface IReputationService {

    ReputationScore calculerScore(Long freelancerId);

    List<ReputationScore> getClassement();

    ReputationScore getScoreByFreelancer(Long freelancerId);
}
