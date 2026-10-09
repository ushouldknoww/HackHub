package it.unicam.hackhub.model;

import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.model.enums.PrizeStatus;
import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.Period;
import it.unicam.hackhub.model.valueobjs.PrizePayout;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hackathons", indexes = {
        @Index(name = "idx_hackathon_status", columnList = "status"),
        @Index(name = "idx_hackathon_start", columnList = "event_start_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column
    private String location;

    @Column(name = "is_online", nullable = false)
    private boolean online;

    @Column(nullable = false)
    private double prize;

    @Column(name = "max_team_size", nullable = false)
    private int maxTeamSize;

    @Column(nullable = false, length = 2000)
    private String regulation;

    @Column(name = "organizer_id", nullable = false)
    private Long organizer;

    @Column(name = "judge_id", nullable = false)
    private Long judge;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "hackathon_mentors",
            joinColumns = @JoinColumn(name = "hackathon_id")
    )
    @Column(name = "staff_profile_id", nullable = false)
    private List<Long> mentors = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "sub_start_at")),
            @AttributeOverride(name = "endAt", column = @Column(name = "sub_end_at"))
    })
    private Period subscriptionDates;

    @Column(nullable = false)
    private String delivery;

    @Enumerated(EnumType.STRING)
    @Column(name = "ranking_policy", nullable = false, length = 80)
    private RankingPolicy rankingPolicy;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "event_start_at")),
            @AttributeOverride(name = "endAt", column = @Column(name = "event_end_at"))
    })
    private Period dates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private HackathonStatus status;

    @Column(name = "winner_participating_team_id")
    private Long winnerParticipatingTeam;

    @Embedded
    private PrizePayout prizePayout;

    public Hackathon(
            String name,
            String type,
            String location,
            boolean online,
            double prize,
            int maxTeamSize,
            String regulation,
            Long organizerId,
            Long judgeId,
            List<Long> mentorsId,
            String delivery,
            RankingPolicy rankingPolicy,
            Period subscriptionDates,
            Period dates
    ) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.online = online;
        this.prize = prize;
        this.maxTeamSize = maxTeamSize;
        this.regulation = regulation;
        this.organizer = organizerId;
        this.judge = judgeId;
        this.mentors = mentorsId != null ? new ArrayList<>(mentorsId) : new ArrayList<>();
        this.delivery = delivery;
        this.rankingPolicy = rankingPolicy;
        this.subscriptionDates = subscriptionDates;
        this.dates = dates;
        this.status = HackathonStatus.IN_REGISTRATION; 
    }

    /** Close participant registration and move the event to its scheduled, ready state. */
    public void closeRegistration() {
        if (this.status != HackathonStatus.IN_REGISTRATION) {
            throw new IllegalStateException("Registration can only be closed while the hackathon is open for registration");
        }
        this.status = HackathonStatus.READY_TO_START;
    }

    /** Begin the live event only after its scheduled start time. */
    public void start() {
        if (this.status != HackathonStatus.READY_TO_START) {
            throw new IllegalStateException("A hackathon must be ready before it can start");
        }
        if (this.dates == null || this.dates.getStartAt() == null || LocalDateTime.now().isBefore(this.dates.getStartAt())) {
            throw new IllegalStateException("The scheduled event start time has not been reached");
        }
        this.status = HackathonStatus.RUNNING;
    }

    /** Open evaluation only after the event's scheduled end time. */
    public void startEvaluation() {
        if (this.status != HackathonStatus.RUNNING) {
            throw new IllegalStateException("Only a running hackathon can enter evaluation");
        }
        if (this.dates == null || this.dates.getEndAt() == null || LocalDateTime.now().isBefore(this.dates.getEndAt())) {
            throw new IllegalStateException("The scheduled event end time has not been reached");
        }
        this.status = HackathonStatus.IN_EVALUATION;
    }

    public void close() {
        if (this.status != HackathonStatus.IN_EVALUATION) {
            throw new IllegalStateException("A hackathon can only be closed after evaluation");
        }
        this.status = HackathonStatus.CLOSED;
    }

    public void declareWinner(Long winnerParticipatingTeamId) {
        this.winnerParticipatingTeam = winnerParticipatingTeamId;
        this.prizePayout = new PrizePayout(PrizeStatus.NOT_PAID, null, null, null);
    }

    public Long getWinnerParticipatingTeamId() {
        return this.winnerParticipatingTeam;
    }

    public PrizeStatus getPrizeStatus() {
        return this.prizePayout != null ? this.prizePayout.getStatus() : null;
    }

    public void confirmPrizePaid(String providerRef, LocalDateTime paidAt) {
        this.prizePayout = new PrizePayout(PrizeStatus.PAID, paidAt, providerRef, null);
    }

    public void markPrizeFailed(String failureReason, LocalDateTime paidAt) {
        this.prizePayout = new PrizePayout(PrizeStatus.FAILED, paidAt, null, failureReason);
    }

    public void closeEvaluations() {
        close();
    }
}