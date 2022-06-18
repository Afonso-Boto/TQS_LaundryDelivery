package tqs.project.laundryplatform.repository;

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
import tqs.project.laundryplatform.model.Laundry;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class LaundryRepositoryTest {

    Laundry laundry;

    @Container
    public static MySQLContainer container =
            new MySQLContainer("mysql:8.0.29")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("123456");

    @Autowired private LaundryRepository laundryRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeEach
    public void setUp() {
        laundry = new Laundry("test_laundry", "123456");
        laundryRepository.save(laundry);
    }

    @AfterEach
    public void tearDown() {
        laundryRepository.deleteAll();
    }

    @Test
    public void testFindById() {
        assertThat(laundryRepository.findById(laundry.getId()).orElse(null)).isEqualTo(laundry);
    }

    @Test
    public void testFindByName() {
        assertThat(laundryRepository.findByName(laundry.getName()).orElse(null)).isEqualTo(laundry);
    }

    @Test
    public void testInvalidFindById() {
        assertThat(laundryRepository.findById(0L).orElse(null)).isNull();
    }

    @Test
    public void testInvalidFindByName() {
        assertThat(laundryRepository.findByName("test_laundry_not_found").orElse(null)).isNull();
    }
}
