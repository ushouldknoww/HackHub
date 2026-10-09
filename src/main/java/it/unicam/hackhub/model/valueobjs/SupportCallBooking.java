package it.unicam.hackhub.model.valueobjs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportCallBooking {

    @Column(name = "call_mentor_id")
    private Long mentor;

    @Column(name = "call_starts_at")
    private LocalDateTime startsAt;

    @Column(name = "call_duration")
    private Duration duration;

    @Column(name = "call_event_id")
    private String eventId;

    @Column(name = "call_meeting_url")
    private String meetingURL;

    @Column(name = "call_created_at")
    private LocalDateTime createdAt;

    public SupportCallBooking(
            Long mentor,
            LocalDateTime startsAt,
            Duration duration,
            String eventId,
            String meetingURL,
            LocalDateTime createdAt
    ) {
        this.mentor = mentor;
        this.startsAt = startsAt;
        this.duration = duration;
        this.eventId = eventId;
        this.meetingURL = meetingURL;
        this.createdAt = createdAt;
    }
}