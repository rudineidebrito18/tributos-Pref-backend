package br.com.tributos.config;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Rate limit simples em memória para endpoints públicos — configurável por propriedade.
 */
@Component
public class PublicApiRateLimitFilter extends OncePerRequestFilter {

    private final PublicApiRateLimitProperties properties;
    private final ConcurrentHashMap<String, AtomicInteger> counts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> windowStart = new ConcurrentHashMap<>();

    public PublicApiRateLimitFilter(PublicApiRateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        Long start = windowStart.get(ip);
        if (start == null || now - start >= properties.windowMs()) {
            windowStart.put(ip, now);
            counts.put(ip, new AtomicInteger(0));
        }

        if (counts.get(ip).incrementAndGet() > properties.maxRequests()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }
        return path.startsWith("/api/public/") || path.startsWith("/api/webhooks/");
    }
}
