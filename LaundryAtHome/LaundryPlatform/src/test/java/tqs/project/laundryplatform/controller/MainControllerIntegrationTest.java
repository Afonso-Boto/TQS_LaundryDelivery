package tqs.project.laundryplatform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MvcResult;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.repository.OrderRepository;

import java.sql.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureMockMvc
public class MainControllerIntegrationTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() throws Exception {
        assertThat(mvc).isNotNull();
        orderRepository.save(new Order(1L, new Date(2022, 12, 12), 20.99));
    }

    @Test
    @DisplayName("GET Request Index")
    void getHome() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/index")).andExpect(status().isFound()).andReturn();

        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo("/login");
    }

    @Test
    @DisplayName("GET Request Login")
    void getLogin() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request logout")
    void getLogout() throws Exception {
        mvc.perform(get("/logout")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request Register")
    void getRegister() throws Exception {
        mvc.perform(get("/register")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request new_order")
    void getNewOrder() throws Exception {
        mvc.perform(get("/new_order")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request orders")
    void getOrders() throws Exception {
        mvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orders"))
                .andExpect(view().name("orders"));
    }

    @Test
    @DisplayName("GET Request orders mobile")
    void getOrdersMobile() throws Exception {
        mvc.perform(get("/orders-mobile"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request service")
    void getService() throws Exception {
        mvc.perform(get("/service")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request ok")
    void getOk() throws Exception {
        mvc.perform(get("/ok")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request tracking")
    void getTracking() throws Exception {
        mvc.perform(get("/tracking").param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("order"))
                .andExpect(view().name("tracking"));
    }

    @Test
    @DisplayName("GET Request tracking mobile")
    void getTrackingMobile() throws Exception {
        mvc.perform(get("/tracking-mobile").param("orderId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Request error")
    void getError() throws Exception {
        mvc.perform(get("/error")).andExpect(status().isOk());
    }
}
