package org.example.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnProperty(name = "bucket4j.enabled", havingValue = "true", matchIfMissing = true)
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final int LIMIT = 10; // 10 requisições por minuto por IP

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/simulacoes") && request.getMethod().equals("POST")) {
            String ip = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, k -> Bucket4j.builder()
                    .addLimit(Bandwidth.classic(LIMIT, Refill.greedy(LIMIT, Duration.ofMinutes(1))))
                    .build());
            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(429);
                response.getWriter().write("Rate limit exceeded. Tente novamente em instantes.");
                return;
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
