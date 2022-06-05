package com.qourier.qourier_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qourier.qourier_app.bids.DeliveriesManager;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.repository.BidsRepository;
import com.qourier.qourier_app.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
public class ApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private DeliveriesManager deliveriesManager;

    private List<Delivery> deliveryList, filteredDeliveryList;

    @BeforeEach
    void setUp() {
        deliveryList =
                List.of(
                        new Delivery(
                                "test0@email.com",
                                10.00,
                                20.00,
                                "Test0 street",
                                "Test0 origin street"),
                        new Delivery(
                                "test1@email.com",
                                11.00,
                                21.00,
                                "Test1 street",
                                "Test1 origin street"),
                        new Delivery(
                                "test2@email.com",
                                12.00,
                                22.00,
                                "Test2 street",
                                "Test2 origin street"),
                        new Delivery(
                                "test0@email.com",
                                13.00,
                                23.00,
                                "Test3 street",
                                "Test3 origin street"));

        filteredDeliveryList =
                List.of(
                        new Delivery(
                                "test0@email.com",
                                10.00,
                                20.00,
                                "Test0 street",
                                "Test0 origin street"),
                        new Delivery(
                                "test0@email.com",
                                13.00,
                                23.00,
                                "Test3 street",
                                "Test3 origin street"));

        when(deliveriesManager.getAllDeliveries()).thenReturn(deliveryList);
        when(deliveriesManager.getDeliveriesFromCustomer("test0@email.com"))
                .thenReturn(filteredDeliveryList);
    }

    @Test
    @DisplayName("Obtain all deliveries")
    void whenGetAllDeliveries_thenReturnAllDeliveries() throws Exception {
        MvcResult result =
                mvc.perform(get("/api/v1/deliveries")).andExpect(status().isOk()).andReturn();

        String resultDeliveriesString = result.getResponse().getContentAsString();
        String expectedDeliveries =
                "[{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test0 street\",\"originAddr\":\"Test0 origin street\",\"riderId\":null,\"latitude\":10.0,\"longitude\":20.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},{\"customerId\":\"test1@email.com\",\"deliveryAddr\":\"Test1 street\",\"originAddr\":\"Test1 origin street\",\"riderId\":null,\"latitude\":11.0,\"longitude\":21.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},{\"customerId\":\"test2@email.com\",\"deliveryAddr\":\"Test2 street\",\"originAddr\":\"Test2 origin street\",\"riderId\":null,\"latitude\":12.0,\"longitude\":22.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test3 street\",\"originAddr\":\"Test3 origin street\",\"riderId\":null,\"latitude\":13.0,\"longitude\":23.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null}]";
        assertEquals(expectedDeliveries, resultDeliveriesString);
    }

    @Test
    @DisplayName("Obtain all deliveries for a given customerId")
    void whenGetFilteredDeliveries_thenReturnFilteredDeliveries() throws Exception {
        MvcResult result =
                mvc.perform(get("/api/v1/deliveries?customerId=test0@email.com"))
                        .andExpect(status().isOk())
                        .andReturn();

        String resultDeliveriesString = result.getResponse().getContentAsString();
        String expectedDeliveries =
                "[{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test0 street\",\"originAddr\":\"Test0 origin street\",\"riderId\":null,\"latitude\":10.0,\"longitude\":20.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test3 street\",\"originAddr\":\"Test3 origin street\",\"riderId\":null,\"latitude\":13.0,\"longitude\":23.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null}]";
        assertEquals(expectedDeliveries, resultDeliveriesString);
    }

    @Test
    @DisplayName("Obtain all deliveries for a given customerId")
    void whenPostDelivery_thenDeliveryIsCreated() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(
                        new Delivery(
                                "test0@email.com",
                                99.00,
                                99.00,
                                "Test3 street",
                                "Test3 origin street"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/deliveries")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isCreated())
                        .andReturn();
    }
}
