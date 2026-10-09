package it.unicam.hackhub.model.dto.response;

import java.time.LocalDateTime;

/** Public, read-only summary of a team's ranked submission. */
public record LeaderboardEntry(
        int rank,
        Long participatingTeamId,
        Long teamId,
        String teamName,
        int submissionScore,
        int penaltyPoints,
        int finalScore,
        LocalDateTime submittedAt,
        LocalDateTime teamRegisteredAt,
        boolean winner
) { }
