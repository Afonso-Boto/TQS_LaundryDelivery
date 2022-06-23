package tqs.project.laundryplatform.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import tqs.project.laundryplatform.model.*;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class OrderRepositoryTest {

    Order order;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderTypeRepository orderTypeRepository;
    @Autowired
    private LaundryRepository laundryRepository;
    @Autowired
    private UserRepository userRepository;

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
        User user = new User("test_user", "test@user.pt", "123456", "test_user", 964023412);
        Laundry laundry = new Laundry("test_laundry", "123456");
        OrderType orderType = new OrderType("test_order_type", 100);
        order = new Order(orderType, user, laundry);
        laundryRepository.save(laundry);
        userRepository.save(user);
        orderTypeRepository.save(orderType);
        orderRepository.save(order);
    }

    @Test
    public void testFindById() {
        assertThat(orderRepository.findById(order.getId()).orElse(null)).isEqualTo(order);
    }

    @Test
    public void testFindByUser() {
        assertThat(orderRepository.findAllByUser(order.getUser())).hasSize(1);
        assertThat(orderRepository.findAllByUser(order.getUser())).contains(order);
    }

    @Test
    public void testInvalidFindByUser() {
        User user = new User("test_user", "w123456", "123456", "test_user", 964023412);
        userRepository.save(user);
        assertThat(orderRepository.findAllByUser(user)).isEmpty();
    }

    @Test
    public void testInvalidId_thenReturnEmptyOptional() {
        assertThat(orderRepository.findById(0L)).isEmpty();
    }
}
