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
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.UserRepository;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class UserRepositoryTest {
    User user;

    @Container
    public static MySQLContainer container =
            new MySQLContainer("mysql:8.0.29")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("123456");

    @Autowired private UserRepository repository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeEach
    public void setUp() {
        user = new User("test_jonas", "afonso@ua.pt", "123", "Jonas Fernandes", 1234567);
        repository.save(user);
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void testFindByUsername() {
        assertThat(repository.findByUsername(user.getUsername()).orElse(null)).isEqualTo(user);
    }

    @Test
    public void whenInvalidUsername_thenReturnEmptyOptional() {
        assertThat(repository.findByUsername("invalid_username")).isEmpty();
    }

    @Test
    public void testFindById() {
        assertThat(repository.findById(user.getId()).orElse(null)).isEqualTo(user);
    }

    @Test
    public void whenInvalidId_thenReturnEmptyOptional() {
        assertThat(repository.findById(0L)).isEmpty();
    }

    @Test
    public void testExistsByUsername() {
        assertThat(repository.existsByUsername(user.getUsername())).isTrue();
    }

    @Test
    public void whenInvalidUsername_thenReturnFalse() {
        assertThat(repository.existsByUsername("invalid_username")).isFalse();
    }
}
