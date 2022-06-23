package tqs.project.laundryplatform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @LocalServerPort int randomServerPort;

    @Autowired private MockMvc mvc;

    @Autowired UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        assertThat(mvc).isNotNull();
        userRepository.save(new User("test", "test@ua.pt", "123", "test", 123));
    }

    @Test
    @DisplayName("GET Request Login")
    void getLogin() throws Exception {
        mvc.perform(post("/auth/login").param("username", "test2").param("password", "123"))
                .andExpect(status().isFound())
                .andExpect(cookie().exists("id"));
    }

    @Test
    @DisplayName("GET Request invalid Login")
    void getInvalidLogin() throws Exception {
        mvc.perform(
                        post("/auth/login")
                                .param("username", "jksaffsdf")
                                .param("password", "123123qd"))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("id"));
    }

    @Test
    @DisplayName("GET Request Register")
    void getRegister() throws Exception {
        mvc.perform(
                        post("/auth/register")
                                .param("username", "test2")
                                .param("email", "test2@ua.pt")
                                .param("password", "123")
                                .param("fullName", "test2")
                                .param("phone", "123"))
                .andExpect(status().isFound());
    }

    @Test
    @DisplayName("GET Request invalid Register")
    void getInvalidRegister() throws Exception {
        mvc.perform(
                        post("/auth/register")
                                .param("username", "test")
                                .param("email", "test@ua.pt")
                                .param("password", "123")
                                .param("fullName", "test")
                                .param("phone", "123"))
                .andExpect(status().isFound())
                .andExpect(cookie().doesNotExist("id"));
    }
}
