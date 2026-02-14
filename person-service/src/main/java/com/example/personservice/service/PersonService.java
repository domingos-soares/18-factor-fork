package com.example.personservice.service;

import com.example.personservice.domain.Person;
import com.example.personservice.repo.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonService {
    private final PersonRepository repository;
    private final Clock clock;

    public PersonService(PersonRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public List<Person> list() {
        return repository.findAll();
    }

    public Optional<Person> get(UUID id) {
        return repository.findById(id);
    }

    public Person create(String firstName, String lastName, String email) {
        Instant now = Instant.now(clock);
        Person person = new Person(UUID.randomUUID(), firstName, lastName, email, now, now);
        return repository.save(person);
    }

    public Optional<Person> update(UUID id, String firstName, String lastName, String email) {
        return repository.findById(id).map(existing -> {
            Instant now = Instant.now(clock);
            Person updated = new Person(existing.id(), firstName, lastName, email, existing.createdAt(), now);
            repository.save(updated);
            return updated;
        });
    }

    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
