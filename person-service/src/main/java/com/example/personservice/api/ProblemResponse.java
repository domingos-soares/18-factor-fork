package com.example.personservice.api;

public record ProblemResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance
) {
}
