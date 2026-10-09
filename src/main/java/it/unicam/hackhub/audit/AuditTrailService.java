package it.unicam.hackhub.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditTrailService {
    private static final Logger log = LoggerFactory.getLogger(AuditTrailService.class);
    private final AuditEventRepository repository;
    public AuditTrailService(AuditEventRepository repository) { this.repository = repository; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Long actorId, String role, String method, String path, String operation, boolean success) {
        try {
            repository.save(new AuditEvent(actorId, role, method, path, operation, success, Instant.now()));
        } catch (RuntimeException exception) {
            // Audit failures are visible in logs but must not turn a completed business operation into a failure.
            log.error("Could not persist audit event for {} {}", method, path, exception);
        }
    }
}
