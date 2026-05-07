package tn.esprit.evaluation.exception;

public class EvaluationAlreadyExistsException extends RuntimeException {

    public EvaluationAlreadyExistsException(Long missionId, Long evaluateurId) {
        super("Une évaluation existe déjà pour la mission " + missionId + " par l'évaluateur " + evaluateurId);
    }
}
