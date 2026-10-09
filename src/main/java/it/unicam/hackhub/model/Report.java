package it.unicam.hackhub.model;

import it.unicam.hackhub.model.enums.ReportResolution;
import it.unicam.hackhub.model.enums.Urgency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hackathon_id", nullable = false)
    private Long hackathon;

    @Column(name = "mentor_id", nullable = false)
    private Long mentor;

    @Column(name = "participating_team_id", nullable = false)
    private Long participatingTeam;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Urgency urgency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean resolved;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_resolution", length = 40)
    private ReportResolution reportResolution;

    public Report(
            Long hackathonId,
            Long mentorId,
            Long participatingTeamId,
            String reason,
            Urgency urgency,
            LocalDateTime createdAt
    ) {
        this.hackathon = hackathonId;
        this.mentor = mentorId;
        this.participatingTeam = participatingTeamId;
        this.reason = reason;
        this.urgency = urgency;
        this.createdAt = createdAt;
        this.resolved = false;
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public void resolve(ReportResolution resolution) {
        this.reportResolution = resolution;
        this.resolved = true;
    }

    public void archive() {
        this.resolved = true;
    }
}