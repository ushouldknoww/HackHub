package it.unicam.hackhub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unicam.hackhub.auth.AccountRole;
import it.unicam.hackhub.auth.AuthAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {
    private final SecretKey signingKey;
    private final long expiresInSeconds;

    public TokenService(
            @Value("${hackhub.jwt.secret}") String secret,
            @Value("${hackhub.jwt.expiration-seconds:3600}") long expiresInSeconds) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 UTF-8 bytes");
        }
        if (expiresInSeconds < 60 || expiresInSeconds > 86_400) {
            throw new IllegalArgumentException("JWT expiration must be between 60 and 86400 seconds");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresInSeconds = expiresInSeconds;
    }

    public String issue(AuthAccount account) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(Long.toString(account.getSubjectId()))
                .claim("accountId", account.getId())
                .claim("username", account.getUsername())
                .claim("role", account.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiresInSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public ActorPrincipal parse(String token) throws JwtException {
        Claims claims = Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload();
        try {
            return new ActorPrincipal(
                    claims.get("accountId", Number.class).longValue(),
                    Long.parseLong(claims.getSubject()),
                    claims.get("username", String.class),
                    AccountRole.valueOf(claims.get("role", String.class))
            );
        } catch (RuntimeException exception) {
            throw new JwtException("Invalid authentication token claims", exception);
        }
    }

    public long getExpiresInSeconds() { return expiresInSeconds; }
}
