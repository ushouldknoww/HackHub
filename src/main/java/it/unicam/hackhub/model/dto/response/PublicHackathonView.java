package it.unicam.hackhub.model.dto.response;

import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.model.enums.PrizeStatus;
import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.Period;

/** Public API representation; deliberately excludes internal payment-provider references and failure details. */
public record PublicHackathonView(
        Long id,
        String name,
        String type,
        String location,
        boolean online,
        double prize,
        int maxTeamSize,
        String regulation,
        Long organizer,
        Long judge,
        Period subscriptionDates,
        Period dates,
        String delivery,
        RankingPolicy rankingPolicy,
        HackathonStatus status,
        Long winnerParticipatingTeamId,
        PrizeStatus prizeStatus
) { }
