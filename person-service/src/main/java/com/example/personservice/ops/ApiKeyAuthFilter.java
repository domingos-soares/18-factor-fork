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

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final boolean required;
    private final String expectedValue;

    public ApiKeyAuthFilter(
            @Value("${person-service.api-key.required:false}") boolean required,
            @Value("${person-service.api-key.value:}") String expectedValue
    ) {
        this.required = required;
        this.expectedValue = expectedValue;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!required) {
            filterChain.doFilter(request, response);
            return;
        }

        if (expectedValue == null || expectedValue.isBlank()) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return;
        }

        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || !apiKey.equals(expectedValue)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
