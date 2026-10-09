package it.unicam.hackhub.utils.strategy;

import it.unicam.hackhub.model.RankingCandidate;

import java.util.Comparator;
import java.util.List;

public class ScoreDescThenEarliestSubmissionStrategy implements RankingStrategy {

    @Override
    public Long selectWinner(List<RankingCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        // Trova il candidato con lo score più alto e, in caso di parità, la sottomissione più vecchia
        return candidates.stream()
                .min(Comparator.comparingInt(RankingCandidate::getFinalScore).reversed()
                        .thenComparing(RankingCandidate::getSubmissionUpdatedAt))
                .map(RankingCandidate::getEligibleParticipatingTeam)
                .orElse(null);
    }
}