package com.example.ocorrencias_escolares_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter simples em memória para o endpoint de login.
 * Permite no máximo MAX_ATTEMPTS tentativas por IP dentro de WINDOW_MS milissegundos.
 *
 * Para produção considere substituir por Redis + Bucket4j para suporte a múltiplas instâncias.
 */
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/auth/login";
    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_MS = 60_000L; // 1 minuto

    private record AttemptBucket(AtomicInteger count, long windowStart) {}

    private final Map<String, AttemptBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !LOGIN_PATH.equals(request.getServletPath())
                || !"POST".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ip = extractIp(request);
        long now = Instant.now().toEpochMilli();

        AttemptBucket bucket = buckets.compute(ip, (key, existing) -> {
            if (existing == null || (now - existing.windowStart()) > WINDOW_MS) {
                return new AttemptBucket(new AtomicInteger(1), now);
            }
            existing.count().incrementAndGet();
            return existing;
        });

        if (bucket.count().get() > MAX_ATTEMPTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"status":429,"message":"Muitas tentativas de login. Aguarde 1 minuto e tente novamente."}
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}