package it.unicam.hackhub.model;

import it.unicam.hackhub.model.enums.SupportRequestStatus;
import it.unicam.hackhub.model.enums.Urgency;
import it.unicam.hackhub.model.valueobjs.SupportCallBooking;
import it.unicam.hackhub.model.valueobjs.SupportReply;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hackathon_id", nullable = false)
    private Long hackathon;

    @Column(name = "participating_team_id", nullable = false)
    private Long participatingTeam;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Urgency urgency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupportRequestStatus status;

    @Embedded
    private SupportReply reply;

    @Embedded
    private SupportCallBooking callBooking;

    public SupportRequest(
            Long hackathonId,
            Long participatingTeamId,
            String title,
            String description,
            Urgency urgency,
            LocalDateTime createdAt
    ) {
        this.hackathon = hackathonId;
        this.participatingTeam = participatingTeamId;
        this.title = title;
        this.description = description;
        this.urgency = urgency;
        this.createdAt = createdAt;
        this.status = SupportRequestStatus.OPEN;
    }

    public boolean isOpen() {
        return this.status == SupportRequestStatus.OPEN;
    }

    public void addReply(Long mentorId, String message, LocalDateTime answeredAt) {
        this.reply = new SupportReply(mentorId, message, answeredAt);
        this.status = SupportRequestStatus.ANSWERED;
    }

    public void scheduleCall(
            Long staffProfileId,
            LocalDateTime startsAt,
            java.time.Duration duration,
            String eventId,
            String meetingURL
    ) {
        this.callBooking = new SupportCallBooking(
                staffProfileId,
                startsAt,
                duration,
                eventId,
                meetingURL,
                LocalDateTime.now()
        );
        this.status = SupportRequestStatus.CALL_SCHEDULED;
    }
}