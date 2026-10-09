package it.unicam.hackhub.model;

import it.unicam.hackhub.model.enums.PayoutMethod;
import it.unicam.hackhub.model.valueobjs.PayoutAccountRef;
import it.unicam.hackhub.model.valueobjs.Penalty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "participating_teams",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_part_team_hackathon", columnNames = {"team_id", "hackathon_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipatingTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long team;

    @Column(name = "hackathon_id", nullable = false)
    private Long hackathon;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "pt_active_members",
            joinColumns = @JoinColumn(name = "participating_team_id")
    )
    @Column(name = "user_id", nullable = false)
    private List<Long> activeMembers = new ArrayList<>();

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Embedded
    private PayoutAccountRef payoutAccountRef;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "pt_penalties",
            joinColumns = @JoinColumn(name = "participating_team_id")
    )
    private List<Penalty> penalties = new ArrayList<>();

    @Column(nullable = false)
    private boolean disqualified;

    public ParticipatingTeam(
            Long hackathonId,
            Long teamId,
            List<Long> memberIdsSnapshot,
            String contactEmail,
            PayoutMethod payoutMethod,
            String payoutRef,
            LocalDateTime now
    ) {
        this.hackathon = hackathonId;
        this.team = teamId;
        this.activeMembers = new ArrayList<>(memberIdsSnapshot);
        this.contactEmail = contactEmail;
        this.payoutAccountRef = new PayoutAccountRef(payoutMethod, payoutRef);
        this.registeredAt = now;
        this.disqualified = false;
    }

    public Penalty applyPenalty(int points, String reason, Long reportId) {
        Penalty penalty = new Penalty(points, reason, LocalDateTime.now(), reportId);
        this.penalties.add(penalty);
        return penalty;
    }

    public void disqualify() {
        this.disqualified = true;
    }

    public boolean isDisqualified() {
        return this.disqualified;
    }

    public int getTotalPenaltyPoints() {
        return this.penalties.stream()
                .mapToInt(Penalty::getPoints)
                .sum();
    }

    public int getTeamSize() {
        return this.activeMembers.size();
    }

    public PayoutAccountRef getPaymentAccountRef() {
        return this.payoutAccountRef;
    }
}