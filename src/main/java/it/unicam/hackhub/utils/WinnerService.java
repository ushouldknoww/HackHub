package it.unicam.hackhub.utils;

import it.unicam.hackhub.model.RankingCandidate;
import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.utils.strategy.RankingStrategy;
import it.unicam.hackhub.utils.strategy.ScoreDescThenEarliestSubmissionStrategy;
import it.unicam.hackhub.utils.strategy.ScoreDescThenMinorTeamSizeThenEarliestSubmission;
import it.unicam.hackhub.utils.strategy.ScoreDescThenTeamRegistrationOrderStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WinnerService {

    public Long selectWinner(RankingPolicy policy, List<RankingCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        RankingStrategy strategy;

        switch (policy) {
            case SCORE_DESC_THEN_EARLIEST_SUBMISSION:
                strategy = new ScoreDescThenEarliestSubmissionStrategy();
                break;
            case SCORE_DESC_THEN_TEAM_REGISTRATION_ORDER:
                strategy = new ScoreDescThenTeamRegistrationOrderStrategy();
                break;
            case SCORE_DESC_THEN_MINOR_TEAM_SIZE_THEN_EARLIEST_SUBMISSION:
                strategy = new ScoreDescThenMinorTeamSizeThenEarliestSubmission();
                break;
            default:
                throw new IllegalArgumentException("Ranking Policy non supportata: " + policy);
        }

        return strategy.selectWinner(candidates);
    }
}