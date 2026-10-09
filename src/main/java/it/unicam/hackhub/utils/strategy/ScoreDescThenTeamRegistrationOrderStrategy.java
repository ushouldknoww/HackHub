package it.unicam.hackhub.utils.strategy;

import it.unicam.hackhub.model.RankingCandidate;

import java.util.Comparator;
import java.util.List;

public class ScoreDescThenTeamRegistrationOrderStrategy implements RankingStrategy {

    @Override
    public Long selectWinner(List<RankingCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        return candidates.stream()
                .min(Comparator.comparingInt(RankingCandidate::getFinalScore).reversed()
                        .thenComparing(RankingCandidate::getTeamRegisteredAt))
                .map(RankingCandidate::getEligibleParticipatingTeam)
                .orElse(null);
    }
}