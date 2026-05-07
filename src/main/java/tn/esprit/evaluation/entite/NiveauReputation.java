package tn.esprit.evaluation.entite;

public enum NiveauReputation {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM;

    public static NiveauReputation fromScore(double score) {
        if (score < 2.5) {
            return BRONZE;
        }
        if (score <= 3.4) {
            return SILVER;
        }
        if (score <= 4.2) {
            return GOLD;
        }
        return PLATINUM;
    }
}
