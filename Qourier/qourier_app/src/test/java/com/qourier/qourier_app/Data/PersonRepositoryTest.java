package com.qourier.qourier_app.Data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class PersonRepositoryTest {
    Person customer, rider, admin;

    // instantiate the container passing selected config
    @Container
    public static MySQLContainer container = new MySQLContainer("mysql:8.0.29")
            .withUsername("demo")
            .withPassword("demopass")
            .withDatabaseName("test_db");

    @Autowired
    private PersonRepository personRepository;

    // read configuration from running db
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @Test
    public void  whenMultiplePeopleAdded_thenAllPeoplePersisted() {
        rider = new Person("Name0", "email0@mail.com", "Password0", 0, "0123456789");
        customer = new Person("Name1", "email1@mail.com", "Password1", 1, "Laundry stuff");
        admin = new Person("Name2", "email2@mail.com", "Password2");

        List<Person> people = List.of(
            rider,
            customer,
            admin
        );

        personRepository.saveAll(people);

        List<Person> peoplePersisted = personRepository.findAll();
        assertThat(peoplePersisted).hasSize(people.size());
        assertThat(peoplePersisted).containsExactlyElementsOf(people);
    }

}
