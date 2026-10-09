package it.unicam.hackhub.audit;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_created_at", columnList = "created_at"),
        @Index(name = "idx_audit_actor", columnList = "actor_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_id") private Long actorId;
    @Column(name = "actor_role", length = 20) private String actorRole;
    @Column(name = "http_method", nullable = false, length = 10) private String httpMethod;
    @Column(name = "request_path", nullable = false, length = 500) private String requestPath;
    @Column(nullable = false, length = 120) private String operation;
    @Column(nullable = false) private boolean success;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    public AuditEvent(Long actorId, String actorRole, String httpMethod, String requestPath,
                      String operation, boolean success, Instant createdAt) {
        this.actorId = actorId;
        this.actorRole = actorRole;
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.operation = operation;
        this.success = success;
        this.createdAt = createdAt;
    }
}
