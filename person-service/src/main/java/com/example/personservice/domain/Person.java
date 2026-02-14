package com.example.personservice.domain;

import java.time.Instant;
import java.util.UUID;

public record Person(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Instant createdAt,
        Instant updatedAt
) {
}
