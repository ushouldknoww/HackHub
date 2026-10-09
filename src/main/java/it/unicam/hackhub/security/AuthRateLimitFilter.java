package it.unicam.hackhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/** Lightweight per-instance throttling for public authentication endpoints. */
@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        int limit;
        long windowMillis;
        if ("/api/auth/login".equals(path)) {
            limit = 10; windowMillis = Duration.ofMinutes(1).toMillis();
        } else if ("/api/auth/register".equals(path) || "/api/auth/staff/register".equals(path)) {
            limit = 5; windowMillis = Duration.ofHours(1).toMillis();
        } else {
            chain.doFilter(request, response);
            return;
        }

        long now = System.currentTimeMillis();
        String key = request.getRemoteAddr() + ":" + path;
        Window window = windows.compute(key, (ignored, current) ->
                current == null || now - current.startedAt >= windowMillis ? new Window(now, 1) : current.increment());
        if (windows.size() > 10_000) {
            windows.entrySet().removeIf(entry -> now - entry.getValue().startedAt > Duration.ofHours(2).toMillis());
        }
        if (window.count > limit) {
            response.setStatus(429);
            response.setHeader("Retry-After", Long.toString(Math.max(1, (window.startedAt + windowMillis - now + 999) / 1000)));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Authentication rate limit exceeded\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    private static final class Window {
        private final long startedAt;
        private final int count;
        private Window(long startedAt, int count) { this.startedAt = startedAt; this.count = count; }
        private Window increment() { return new Window(startedAt, count + 1); }
    }
}
