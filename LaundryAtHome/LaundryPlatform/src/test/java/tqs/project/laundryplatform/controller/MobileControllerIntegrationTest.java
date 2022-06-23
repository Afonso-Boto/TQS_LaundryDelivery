package tqs.project.laundryplatform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tqs.project.laundryplatform.account.AccountManager;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureMockMvc
public class MobileControllerIntegrationTest {

    @LocalServerPort int randomServerPort;

    @Autowired private MockMvc mvc;

    @Autowired AccountManager accountManager;

    @Autowired OrderRepository orderRepository;

    @Autowired UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        assertThat(mvc).isNotNull();
        userRepository.save(new User("test", "test@ua.pt", "123", "test", 123));
        orderRepository.save(new Order(1L, new Date(2022, 12, 12), 20.99));
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("register a new user")
    public void registerUser() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "test2");
        json.put("email", "test2@ua.pt");
        json.put("password", "123");
        json.put("fullName", "test2");
        json.put("phoneNumber", "123");

        mvc.perform(
                        post("/mobile/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.valueOf(json)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("register invalid user")
    public void registerInvalidUser() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "test");
        json.put("email", "test2@ua.pt");
        json.put("password", "123");
        json.put("fullName", "test2");
        json.put("phoneNumber", "123");

        mvc.perform(
                        post("/mobile/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.valueOf(json)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("login a user")
    public void loginUser() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "test");
        json.put("password", "123");

        mvc.perform(
                        post("/mobile/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.valueOf(json)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("login invalid user")
    public void loginInvalidUser() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username", "dsadsad");
        json.put("password", "12safdfads3");

        mvc.perform(
                        post("/mobile/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.valueOf(json)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("logout")
    public void logout() throws Exception {
        mvc.perform(get("/mobile/auth/logout")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("orders admin")
    public void ordersAdmin() throws Exception {
        mvc.perform(get("/mobile/orders?username=admin")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("orders test")
    public void ordersTest() throws Exception {
        mvc.perform(get("/mobile/orders?username=test")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("orders tracking")
    public void ordersTracking() throws Exception {
        mvc.perform(get("/mobile/tracking?orderId=1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("orders tracking invalid")
    public void ordersTrackingInvalid() throws Exception {
        MvcResult mvcResult =
                mvc.perform(get("/mobile/tracking?orderId=2"))
                        .andExpect(status().isOk())
                        .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("");
    }
}
