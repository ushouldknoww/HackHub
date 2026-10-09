package it.unicam.hackhub.audit;

import it.unicam.hackhub.security.ActorPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditLoggingAspect.class);
    private final AuditTrailService auditTrailService;
    public AuditLoggingAspect(AuditTrailService auditTrailService) { this.auditTrailService = auditTrailService; }

    @Around("within(it.unicam.hackhub.controller..*)")
    public Object auditMutatingControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) return joinPoint.proceed();
        HttpServletRequest request = servletAttributes.getRequest();
        String method = request.getMethod();
        if (!("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method))) {
            return joinPoint.proceed();
        }

        Long actorId = null;
        String role = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof ActorPrincipal actor) {
            actorId = actor.getSubjectId();
            role = actor.getRole().name();
        }

        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            String operation = joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
            try {
                auditTrailService.record(actorId, role, method, request.getRequestURI(), operation, success);
            } catch (RuntimeException auditException) {
                // The audit path must never turn a successful business request into a failed HTTP response.
                log.error("Could not record audit event for {} {}", method, request.getRequestURI(), auditException);
            }
        }
    }
}
