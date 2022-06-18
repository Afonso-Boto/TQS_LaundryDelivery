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
import tqs.project.laundryplatform.model.Complaint;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@Testcontainers
@SpringBootTest
public class ComplaintRepositoryTest {

    @Autowired
    private ComplaintRepository complaintRepository;

    Complaint complaint;

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
        complaint = new Complaint("test_complaint", "123456");
        complaintRepository.save(complaint);
    }

    @AfterEach
    public void tearDown() {
        complaintRepository.deleteAll();
    }

    @Test
    public void testFindById() {
        assertThat(complaintRepository.findById(complaint.getId()).orElse(null)).isEqualTo(complaint);
    }

    @Test
    public void testFindByTitle() {
        assertThat(complaintRepository.findByTitle(complaint.getTitle()).orElse(null)).isEqualTo(complaint);
    }

    @Test
    public void testInvalidFindById() {
        assertThat(complaintRepository.findById(0L).orElse(null)).isNull();
    }

    @Test
    public void testInvalidFindByTitle() {
        assertThat(complaintRepository.findByTitle("").orElse(null)).isNull();
    }

}
