package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Delivery;
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

@TestPropertySource(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create",
            "spring.datasource.adminemail=test@email.com",
            "spring.datasource.adminpass=test"
        })
@Testcontainers
@SpringBootTest
public class DeliveryRepositoryTest {

    @Container
    public static MySQLContainer container =
            new MySQLContainer("mysql:8.0.29")
                    .withUsername("demo")
                    .withPassword("demopass")
                    .withDatabaseName("test_db");

    @Autowired private DeliveryRepository deliveryRepository;

    // read configuration from running db
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @BeforeEach
    public void setUp() {
        deliveryRepository.save(
                new Delivery(
                        "test0@email.com", 10.00, 20.00, "Test0 street", "Test0 origin street"));
        deliveryRepository.save(
                new Delivery(
                        "test1@email.com", 11.00, 21.00, "Test1 street", "Test1 origin street"));
        deliveryRepository.save(
                new Delivery(
                        "test2@email.com", 12.00, 22.00, "Test2 street", "Test2 origin street"));
        deliveryRepository.save(
                new Delivery(
                        "test0@email.com", 13.00, 23.00, "Test3 street", "Test3 origin street"));
    }

    @AfterEach
    public void cleanUp() {
        deliveryRepository.deleteAll();
    }

    @Test
    void whenDeliveryCustomer_Id_thenReturnDeliveries() {

        List<Delivery> deliveriesFound0 = deliveryRepository.findByCustomerId("test0@email.com");
        assertThat(deliveriesFound0).hasSize(2);

        List<Delivery> deliveriesFound1 = deliveryRepository.findByCustomerId("test1@email.com");
        assertThat(deliveriesFound1).hasSize(1);
        assertThat(deliveriesFound1.get(0).getDeliveryAddr()).isEqualTo("Test1 street");
    }

    @Test
    void whenInvalidCustomerId_thenReturnNull() {
        List<Delivery> deliveriesFound = deliveryRepository.findByCustomerId("wrong@email.com");
        assertThat(deliveriesFound).isEmpty();
    }

    @Test
    void whenMultipleDeliveriesAdded_thenAllDeliveriesPersisted() {
        List<Delivery> deliveriesPersisted = deliveryRepository.findAll();
        assertThat(deliveriesPersisted).hasSize(4);
    }
}
