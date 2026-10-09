package it.unicam.hackhub.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        AccountRole role,
        Long subjectId
) { }
