package com.qourier.qourier_app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.qourier.qourier_app.data.Bid;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestPropertySource(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create",
            "spring.datasource.adminemail=test@email.com",
            "spring.datasource.adminpass=test"
        })
@Testcontainers
@SpringBootTest
public class BidsRepositoryTest {
    @Container
    public static MySQLContainer container =
            new MySQLContainer("mysql:8.0.29")
                    .withUsername("demo")
                    .withPassword("demopass")
                    .withDatabaseName("test_db");

    @Autowired private BidsRepository bidsRepository;

    // read configuration from running db
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @BeforeEach
    public void setUp() {
        bidsRepository.save(new Bid("test0@email.com", 1L, Double.MAX_VALUE));
        bidsRepository.save(new Bid("test1@email.com", 1L, 5.0));
        bidsRepository.save(new Bid("test2@email.com", 1L, 4.0));
        bidsRepository.save(new Bid("test3@email.com", 1L, 3.0));
        bidsRepository.save(new Bid("test4@email.com", 1L, 2.0));
        bidsRepository.save(new Bid("test5@email.com", 1L, 1.0));
    }

    @AfterEach
    public void cleanUp() {
        bidsRepository.deleteAll();
    }

    @Test
    void whenMultipleDeliveriesAdded_thenAllDeliveriesPersisted() {
        List<Bid> bidsPersisted = bidsRepository.findAll();
        assertThat(bidsPersisted).hasSize(6);
    }

    @Test
    void whenFindByRidersId_thenReturnBid() {
        Bid bid = bidsRepository.findByRidersId("test0@email.com");
        assertThat(bid.getRidersId()).isNotNull();
    }

    @Test
    void whenFindByDeliveriesId_thenReturnDeliveries() {
        List<Bid> bids = bidsRepository.findByDeliveryId(1L);
        assertThat(bids).hasSize(6);
    }

    @Test
    void whenFindByDeliveriesIdSorted_thenReturnSortedDeliveries() {
        List<Bid> bids =
                bidsRepository.findByDeliveryId(1L, Sort.by(Sort.Direction.ASC, "distance"));
        assertThat(bids.get(0).getRidersId()).isEqualTo("test5@email.com");
        assertThat(bids.get(1).getRidersId()).isEqualTo("test4@email.com");
        assertThat(bids.get(2).getRidersId()).isEqualTo("test3@email.com");
        assertThat(bids.get(3).getRidersId()).isEqualTo("test2@email.com");
        assertThat(bids.get(4).getRidersId()).isEqualTo("test1@email.com");
        assertThat(bids.get(5).getRidersId()).isEqualTo("test0@email.com");
    }
}
