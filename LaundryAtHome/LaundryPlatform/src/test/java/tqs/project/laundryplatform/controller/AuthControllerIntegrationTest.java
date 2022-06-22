package tqs.project.laundryplatform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
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
    @DisplayName("GET Request Login mobile")
    @Disabled
    void getLoginMobile() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "test2");
        json.put("password", "123");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String param = objectMapper.writeValueAsString(json);

        mvc.perform(post("/auth/login-mobile").param("username", "test2").param("password", "123"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("id"));
    }

    @Test
    @DisplayName("GET Request invalid Login mobile")
    @Disabled
    void getLoginMobileInvalid() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "dsadasdas");
        json.put("password", "1qeweqw23");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String param = objectMapper.writeValueAsString(json);

        mvc.perform(
                        post("/auth/login-mobile")
                                .param("username", "dsadasdas")
                                .param("password", "1qeweqw23"))
                .andExpect(status().isUnauthorized());
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

    @Test
    @DisplayName("GET Request register mobile")
    @Disabled
    void getRegisterMobile() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "test2");
        json.put("email", "test2@ua.pt");
        json.put("password", "123");
        json.put("fullName", "test2");
        json.put("phone", "123");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String param = objectMapper.writeValueAsString(json);

        mvc.perform(
                        post("/auth/register-mobile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(param))
                .andExpect(status().isFound());
    }

    @Test
    @DisplayName("GET Request invalid register mobile")
    void getInvalidRegisterMobile() throws Exception {
        mvc.perform(
                        post("/auth/register-mobile")
                                .param("username", "test")
                                .param("email", "test@ua.pt")
                                .param("password", "123")
                                .param("fullName", "test")
                                .param("phone", "123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET Request logout mobile")
    void getLogoutMobile() throws Exception {
        mvc.perform(get("/auth/logout-mobile")).andExpect(status().isOk());
    }
}
