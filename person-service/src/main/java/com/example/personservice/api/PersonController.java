package com.example.personservice.api;

import com.example.personservice.api.dto.CreatePersonRequest;
import com.example.personservice.api.dto.PersonResponse;
import com.example.personservice.api.dto.UpdatePersonRequest;
import com.example.personservice.domain.Person;
import com.example.personservice.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @GetMapping
    public List<PersonResponse> listPersons() {
        return service.list().stream().map(PersonController::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@Valid @RequestBody CreatePersonRequest body) {
        Person created = service.create(body.firstName(), body.lastName(), body.email());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/persons/" + created.id()))
                .body(toResponse(created));
    }

    @GetMapping("/{id}")
    public PersonResponse getPerson(@PathVariable UUID id) {
        return service.get(id).map(PersonController::toResponse).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @PutMapping("/{id}")
    public PersonResponse updatePerson(@PathVariable UUID id, @Valid @RequestBody UpdatePersonRequest body) {
        return service.update(id, body.firstName(), body.lastName(), body.email())
                .map(PersonController::toResponse)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable UUID id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            throw new PersonNotFoundException(id);
        }
    }

    private static PersonResponse toResponse(Person p) {
        return new PersonResponse(p.id(), p.firstName(), p.lastName(), p.email(), p.createdAt(), p.updatedAt());
    }
}
