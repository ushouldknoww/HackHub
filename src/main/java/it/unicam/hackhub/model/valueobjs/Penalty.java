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
public class Penalty {

    @Column(nullable = false)
    private int points;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "report_id", nullable = false)
    private Long report;

    public Penalty(int points, String reason, LocalDateTime assignedAt, Long reportId) {
        this.points = points;
        this.reason = reason;
        this.assignedAt = assignedAt;
        this.report = reportId;
    }
}