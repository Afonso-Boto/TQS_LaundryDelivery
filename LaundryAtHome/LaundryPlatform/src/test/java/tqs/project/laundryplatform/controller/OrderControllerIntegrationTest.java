package tqs.project.laundryplatform.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.repository.OrderRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

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
    @DisplayName("init-order")
    void initOrder() throws Exception {
        mvc.perform(get("/order/init-order").param("orderTypeId", "1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("make-order")
    void makeOrder() throws Exception {
        JSONObject json = new JSONObject();
        json.put("itemType", 1);
        json.put("isDark", "Claras");
        json.put("number", 10);

        JSONObject json2 = new JSONObject();
        List<JSONObject> items = new ArrayList<>();
        items.add(json);

        json2.put("its", items);
        System.err.println(json2.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String param = objectMapper.writeValueAsString(json2);

        mvc.perform(
                        post("/order/make-order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(param))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("complaint")
    void complaint() throws Exception {
        JSONObject json = new JSONObject();
        json.put("orderId", 1);
        json.put("title", "test");
        json.put("description", "test");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String param = objectMapper.writeValueAsString(json);

        mvc.perform(post("/order/complaint").contentType(MediaType.APPLICATION_JSON).content(param))
                .andExpect(status().isFound());
    }

    @Test
    @DisplayName("cancel order")
    void cancelOrder() throws Exception {
        mvc.perform(post("/order/cancelOrder/1")).andExpect(status().isFound());
    }
}
