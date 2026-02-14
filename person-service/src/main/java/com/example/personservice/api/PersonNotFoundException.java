package com.example.personservice.api;

import java.util.UUID;

public class PersonNotFoundException extends RuntimeException {
    private final UUID id;

    public PersonNotFoundException(UUID id) {
        super("Person not found: " + id);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
