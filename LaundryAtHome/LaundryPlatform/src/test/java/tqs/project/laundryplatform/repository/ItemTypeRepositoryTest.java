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
import tqs.project.laundryplatform.model.ItemType;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class ItemTypeRepositoryTest {

    ItemType itemType;

    @Container
    public static MySQLContainer container =
            new MySQLContainer("mysql:8.0.29")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("123456");

    @Autowired
    private ItemTypeRepository itemTypeRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeEach
    public void setUp() {
        itemType = new ItemType("test_item_type", 300);
        itemTypeRepository.saveAndFlush(itemType);
    }

    @AfterEach
    public void tearDown() {
        itemTypeRepository.deleteAll();
    }

    @Test
    public void testFindById() {
        assertThat(itemTypeRepository.findById(itemType.getId()).orElse(null)).isEqualTo(itemType);
    }

    @Test
    public void testFindByName() {
        assertThat(itemTypeRepository.findByName(itemType.getName()).orElse(null))
                .isEqualTo(itemType);
    }

    @Test
    public void testFindByNameNotFound() {
        assertThat(itemTypeRepository.findByName("test_item_type_not_found").orElse(null)).isNull();
    }

    @Test
    public void testInvalidId_thenReturnEmptyOptional() {
        assertThat(itemTypeRepository.findById(0).orElse(null)).isNull();
    }
}
