package org.example.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.example.exception.RateLimitException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {
    private static final int MAX_REQUESTS_PER_MINUTE = 10; // ajuste conforme necessário
    private static final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String path = httpRequest.getRequestURI();
            // Ignora endpoints de monitoramento
            if (path.startsWith("/actuator")) {
                chain.doFilter(request, response);
                return;
            }
            String ip = httpRequest.getRemoteAddr();
            long now = System.currentTimeMillis();
            RequestCounter counter = requestCounts.computeIfAbsent(ip, k -> new RequestCounter(now));
            synchronized (counter) {
                if (now - counter.timestamp > 60_000) {
                    counter.count = 1;
                    counter.timestamp = now;
                } else {
                    counter.count++;
                }
                if (counter.count > MAX_REQUESTS_PER_MINUTE) {
                    throw new RateLimitException("Limite de requisições excedido");
                }
            }
        }
        chain.doFilter(request, response);
    }

    // Método auxiliar para testes: limpa o contador de requisições
    public static void resetRequestCounts() {
        requestCounts.clear();
    }

    private static class RequestCounter {
        int count;
        long timestamp;
        RequestCounter(long timestamp) {
            this.count = 1;
            this.timestamp = timestamp;
        }
    }
}
