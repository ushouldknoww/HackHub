package it.unicam.hackhub.config;

import it.unicam.hackhub.utils.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalidBody(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct().collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Request validation failed", details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail invalidParameter(MethodArgumentTypeMismatchException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request parameter",
                "Parameter '" + exception.getName() + "' has an invalid value.");
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ProblemDetail badRequest(Exception exception) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", safeMessage(exception));
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail domainError(DomainException exception) {
        String message = safeMessage(exception);
        String normalized = message.toLowerCase(java.util.Locale.ROOT);
        HttpStatus status = normalized.contains("non trovat") || normalized.contains("non esiste")
                || normalized.contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return problem(status, "Business rule rejected the request", message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail conflict(DataIntegrityViolationException exception) {
        return problem(HttpStatus.CONFLICT, "Resource conflict", "The request conflicts with an existing resource or database constraint.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail unauthenticated(Exception exception) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed", "Invalid credentials or authentication token.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail forbidden(Exception exception) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to perform this operation.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled API error for {} {}", request.getMethod(), request.getRequestURI(), exception);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred.");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(status, detail == null ? "Invalid request" : detail);
        result.setTitle(title);
        result.setType(URI.create("https://httpstatuses.com/" + status.value()));
        result.setProperty("timestamp", Instant.now().toString());
        return result;
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) return "Request could not be processed";
        return message.length() > 1000 ? message.substring(0, 1000) : message;
    }
}
