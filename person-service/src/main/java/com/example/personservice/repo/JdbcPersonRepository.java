package com.example.personservice.repo;

import com.example.personservice.domain.Person;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(prefix = "person-service.repository", name = "type", havingValue = "jdbc")
public class JdbcPersonRepository implements PersonRepository {

    private final JdbcTemplate jdbc;

    public JdbcPersonRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Person> MAPPER = new RowMapper<>() {
        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Person(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toInstant(),
                    rs.getTimestamp("updated_at").toInstant()
            );
        }
    };

    @Override
    public List<Person> findAll() {
        return jdbc.query("select id, first_name, last_name, email, created_at, updated_at from persons order by created_at desc", MAPPER);
    }

    @Override
    public Optional<Person> findById(UUID id) {
        List<Person> results = jdbc.query(
                "select id, first_name, last_name, email, created_at, updated_at from persons where id = ?",
                MAPPER,
                id.toString()
        );
        return results.stream().findFirst();
    }

    @Override
    public Person save(Person person) {
        int updated = jdbc.update(
                "update persons set first_name = ?, last_name = ?, email = ?, updated_at = ? where id = ?",
                person.firstName(),
                person.lastName(),
                person.email(),
                java.sql.Timestamp.from(person.updatedAt()),
                person.id().toString()
        );

        if (updated == 0) {
            jdbc.update(
                    "insert into persons(id, first_name, last_name, email, created_at, updated_at) values (?, ?, ?, ?, ?, ?)",
                    person.id().toString(),
                    person.firstName(),
                    person.lastName(),
                    person.email(),
                    java.sql.Timestamp.from(person.createdAt()),
                    java.sql.Timestamp.from(person.updatedAt())
            );
        }

        return person;
    }

    @Override
    public boolean deleteById(UUID id) {
        return jdbc.update("delete from persons where id = ?", id.toString()) > 0;
    }
}
