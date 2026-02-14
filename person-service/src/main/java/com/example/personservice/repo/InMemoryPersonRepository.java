package com.example.personservice.repo;

import com.example.personservice.domain.Person;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(prefix = "person-service.repository", name = "type", havingValue = "inmemory", matchIfMissing = true)
public class InMemoryPersonRepository implements PersonRepository {
    private final ConcurrentHashMap<UUID, Person> store = new ConcurrentHashMap<>();

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Person> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Person save(Person person) {
        store.put(person.id(), person);
        return person;
    }

    @Override
    public boolean deleteById(UUID id) {
        return store.remove(id) != null;
    }
}
