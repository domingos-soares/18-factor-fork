package com.example.personservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "person-service")
public record PersonServiceProperties(
        ApiKey apiKey,
        RateLimit rateLimit,
        CostBudget costBudget,
        Repository repository
) {
    public record ApiKey(boolean required, String value) {}

    public record RateLimit(long requestsPerSecond) {}

    public record CostBudget(double maxCostPerRequestUsd) {}

    public record Repository(String type) {}
}
