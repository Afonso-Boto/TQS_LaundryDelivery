package com.qourier.qourier_app.repository;


import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.Admin;
import com.qourier.qourier_app.data.Customer;
import com.qourier.qourier_app.data.Rider;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.adminemail=test@gmail.com",
        "spring.datasource.adminpass=test"
})
@Testcontainers
@SpringBootTest
class AccountRepositoryTest {
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
    @BeforeEach
    public void setUp() {
        // Rider
        rider = new Rider(new Account("Name0", "email0@mail.com", "Password0"),
                "0123456789");
        riderRepository.save(rider);

        // Customer
        customer = new Customer(new Account("Name1", "email1@mail.com", "Password1"),
                "Laundry stuff");
        customerRepository.save(customer);

        // Admin
        admin = new Admin(new Account("Name2", "email2@mail.com", "Password2"));
        adminRepository.save(admin);
    }

    @AfterEach
    public void cleanUp() {
        riderRepository.deleteAll();
        customerRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void  whenFindPeopleName_thenReturnPerson() {
        // Rider
        List<Rider> riderPersisted = riderRepository.findByAccount_Name("Name0");
        assertThat(riderPersisted).hasSize(1);
        assertThat(riderPersisted.get(0).getEmail()).isEqualTo(rider.getEmail());

        // Customer
        List<Customer> customerPersisted = customerRepository.findByAccount_Name("Name1");
        assertThat(customerPersisted).hasSize(1);
        assertThat(customerPersisted.get(0).getEmail()).isEqualTo(customer.getEmail());

        // Admin
        List<Admin> adminPersisted = adminRepository.findByAccount_Name("Name2");
        assertThat(adminPersisted).hasSize(1);
        assertThat(adminPersisted.get(0).getEmail()).isEqualTo(admin.getEmail());
    }

    @Test
    void  whenInvalidPersonName_thenReturnNull() {
        // Customer
        List<Customer> fromRepo0 = customerRepository.findByAccount_Name("notName");
        assertThat( fromRepo0 ).isEmpty();

        // Admin
        List<Admin> fromRepo1 = adminRepository.findByAccount_Name("notName");
        assertThat( fromRepo1 ).isEmpty();

        // Rider
        List<Rider> fromRepo2 = riderRepository.findByAccount_Name("notName");
        assertThat( fromRepo2 ).isEmpty();
    }

    @Test
    void  whenMultiplePersonAdded_thenAllPeoplePersisted() {
        // Rider
        List<Rider> riderPersisted = riderRepository.findAll();
        assertThat(riderPersisted).hasSize(1);
        assertThat(riderPersisted.get(0).getAccount().getName()).isEqualTo("Name0");

        // Customer
        List<Customer> customerPersisted = customerRepository.findAll();
        assertThat(customerPersisted).hasSize(1);
        assertThat(customerPersisted.get(0).getAccount().getName()).isEqualTo("Name1");

        // Admin
        List<Admin> adminPersisted = adminRepository.findAll();
        assertThat(adminPersisted).hasSize(1);
        assertThat(adminPersisted.get(0).getAccount().getName()).isEqualTo("Name2");
    }
}
