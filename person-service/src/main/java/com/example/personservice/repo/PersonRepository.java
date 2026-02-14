package com.example.personservice.repo;

import com.example.personservice.domain.Person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository {
    List<Person> findAll();

    Optional<Person> findById(UUID id);

    Person save(Person person);

    boolean deleteById(UUID id);
}
