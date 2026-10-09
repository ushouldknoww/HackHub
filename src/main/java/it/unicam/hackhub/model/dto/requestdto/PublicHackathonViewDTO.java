package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.model.enums.RankingPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicHackathonViewDTO {

    private Long id;
    private String name;
    private String type;
    private String location;
    private double prize;
    private Long organizerId;
    private String organizerName;
    private String organizerSurname;
    private LocalDateTime startSubscriptionDates;
    private LocalDateTime endSubscriptionDates;
    private LocalDateTime startDates;
    private LocalDateTime endDates;
    private HackathonStatus status;
    private int maxTeamSize;
    private String regulation;
    private RankingPolicy rankingPolicy;
    private String delivery;
    private Long winnerParticipatingTeamId;

}