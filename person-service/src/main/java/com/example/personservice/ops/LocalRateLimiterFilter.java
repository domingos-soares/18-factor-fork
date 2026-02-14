package com.example.personservice.ops;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class LocalRateLimiterFilter extends OncePerRequestFilter {

    private final long rps;
    private final AtomicLong windowSecond = new AtomicLong(0);
    private final AtomicLong count = new AtomicLong(0);

    public LocalRateLimiterFilter(@Value("${person-service.rate-limit.requests-per-second:50}") long rps) {
        this.rps = Math.max(1, rps);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long nowSecond = System.currentTimeMillis() / 1000;
        long prevSecond = windowSecond.get();
        if (prevSecond != nowSecond) {
            if (windowSecond.compareAndSet(prevSecond, nowSecond)) {
                count.set(0);
            }
        }

        long n = count.incrementAndGet();
        if (n > rps) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
