package tqs.project.laundryplatform.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import tqs.project.laundryplatform.model.OrderType;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class OrderTypeRepositoryTest {

    OrderType orderType;

    @Autowired private OrderTypeRepository orderTypeRepository;

    @Container
    public static MySQLContainer container =
            new MySQLContainer("mysql:8.0.29")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("123456");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeEach
    public void setUp() {
        orderType = new OrderType("test_order_type", 100);
        orderTypeRepository.save(orderType);
    }

    @AfterEach
    public void tearDown() {
        orderTypeRepository.delete(orderType);
    }

    @Test
    public void testFindById() {
        assertThat(orderTypeRepository.findById(orderType.getId()).orElse(null))
                .isEqualTo(orderType);
    }

    @Test
    public void testFindByName() {
        assertThat(orderTypeRepository.findByName(orderType.getName()).orElse(null))
                .isEqualTo(orderType);
    }

    @Test
    public void testInvalidFindByName() {
        assertThat(orderTypeRepository.findByName("invalid_name").orElse(null)).isNull();
    }

    @Test
    public void testInvalidFindById() {
        assertThat(orderTypeRepository.findById(0L).orElse(null)).isNull();
    }
}
