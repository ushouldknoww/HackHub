package it.unicam.hackhub.model;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class RankingCandidate {

    private final Long eligibleParticipatingTeam;
    private final int finalScore;
    private final LocalDateTime submissionUpdatedAt;
    private final LocalDateTime teamRegisteredAt;
    private final int teamSize;

    public RankingCandidate(
            Long eligibleParticipatingTeamId,
            int finalScore,
            LocalDateTime submissionUpdatedAt,
            LocalDateTime teamRegisteredAt,
            int teamSize
    ) {
        this.eligibleParticipatingTeam = eligibleParticipatingTeamId;
        this.finalScore = finalScore;
        this.submissionUpdatedAt = submissionUpdatedAt;
        this.teamRegisteredAt = teamRegisteredAt;
        this.teamSize = teamSize;
    }
}