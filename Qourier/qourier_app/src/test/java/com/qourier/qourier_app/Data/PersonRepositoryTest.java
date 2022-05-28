package com.qourier.qourier_app.Data;


import org.junit.jupiter.api.Test;
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
    Customer customer;
    Rider rider;
    Admin admin;

    @Container
    public static MySQLContainer container = new MySQLContainer("mysql:8.0.29")
            .withUsername("demo")
            .withPassword("demopass")
            .withDatabaseName("test_db");

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private RiderRepository riderRepository;

    // read configuration from running db
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @Test
    public void  whenMultiplePeopleAdded_thenAllPeoplePersisted() {
        // Rider
        rider = new Rider("Name0", "email0@mail.com", "Password0", "0123456789");
        riderRepository.save(rider);
        List<Rider> riderPersisted = riderRepository.findAll();
        assertThat(riderPersisted).hasSize(1);
        assertThat(riderPersisted.get(0).getName()).isEqualTo("Name0");

        // Customer
        customer = new Customer("Name1", "email1@mail.com", "Password1", "Laundry stuff");
        customerRepository.save(customer);
        List<Customer> customerPersisted = customerRepository.findAll();
        assertThat(customerPersisted).hasSize(1);
        assertThat(customerPersisted.get(0).getName()).isEqualTo("Name1");

        // Admin
        admin = new Admin("Name2", "email2@mail.com", "Password2");
        adminRepository.save(admin);
        List<Admin> adminPersisted = adminRepository.findAll();
        assertThat(adminPersisted).hasSize(1);
        assertThat(adminPersisted.get(0).getName()).isEqualTo("Name2");
    }

}
