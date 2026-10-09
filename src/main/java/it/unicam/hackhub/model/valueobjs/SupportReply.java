package it.unicam.hackhub.model.valueobjs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportReply {

    @Column(name = "reply_mentor_id")
    private Long mentor;

    @Column(name = "reply_message", length = 2000)
    private String message;

    @Column(name = "reply_answered_at")
    private LocalDateTime answeredAt;

    public SupportReply(Long mentorId, String message, LocalDateTime answeredAt) {
        this.mentor = mentorId;
        this.message = message;
        this.answeredAt = answeredAt;
    }
}