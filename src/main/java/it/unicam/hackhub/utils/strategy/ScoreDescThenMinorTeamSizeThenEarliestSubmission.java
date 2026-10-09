package it.unicam.hackhub.utils.strategy;

import it.unicam.hackhub.model.RankingCandidate;

import java.util.Comparator;
import java.util.List;

public class ScoreDescThenMinorTeamSizeThenEarliestSubmission implements RankingStrategy {

    @Override
    public Long selectWinner(List<RankingCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        return candidates.stream()
                .min(Comparator.comparingInt(RankingCandidate::getFinalScore).reversed()
                        .thenComparingInt(RankingCandidate::getTeamSize)
                        .thenComparing(RankingCandidate::getSubmissionUpdatedAt))
                .map(RankingCandidate::getEligibleParticipatingTeam)
                .orElse(null);
    }
}