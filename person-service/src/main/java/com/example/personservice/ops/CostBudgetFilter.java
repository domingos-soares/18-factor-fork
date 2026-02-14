package com.example.personservice.ops;

import com.example.personservice.config.PersonServiceProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CostBudgetFilter extends OncePerRequestFilter {
    private final PersonServiceProperties properties;

    public CostBudgetFilter(PersonServiceProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        double max = properties.costBudget().maxCostPerRequestUsd();
        double estimated = estimateCostUsd(request);

        if (estimated > max) {
            response.setStatus(HttpStatus.PAYMENT_REQUIRED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static double estimateCostUsd(HttpServletRequest request) {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            return 0.001;
        }
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            return 0.003;
        }
        if ("DELETE".equalsIgnoreCase(method)) {
            return 0.002;
        }
        return 0.001;
    }
}
