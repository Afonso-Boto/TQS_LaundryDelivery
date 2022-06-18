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
import tqs.project.laundryplatform.model.Item;
import tqs.project.laundryplatform.model.ItemType;
import tqs.project.laundryplatform.model.Order;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class ItemRepositoryTest {

    Item item;

    @Container
    public static MySQLContainer container =
            new MySQLContainer("mysql:8.0.29")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("123456");

    @Autowired private ItemRepository itemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ItemTypeRepository itemTypeRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeEach
    public void setUp() {
        Order order = new Order();
        ItemType itemType = new ItemType("test_item_type", 300);
        item = new Item(2, false, order, itemType);
        orderRepository.saveAndFlush(order);
        itemTypeRepository.saveAndFlush(itemType);
        itemRepository.saveAndFlush(item);
    }

    @Test
    public void testFindById() {
        assertThat(itemRepository.findById(item.getId()).orElse(null)).isEqualTo(item);
    }

    @Test
    public void testInvalidId_thenReturnEmptyOptional() {
        assertThat(itemRepository.findById(0L)).isEmpty();
    }

    @AfterEach
    public void tearDown() {
        itemRepository.deleteAll();
    }
}
