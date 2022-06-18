package com.qourier.qourier_app.controller;

import static com.qourier.qourier_app.account.login.LoginResult.*;
import static com.qourier.qourier_app.data.DeliveryState.SHIPPED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.account.login.LoginRequest;
import com.qourier.qourier_app.account.register.CustomerRegisterRequest;
import com.qourier.qourier_app.account.register.RiderRegisterRequest;
import com.qourier.qourier_app.bids.DeliveriesManager;
import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.Bid;
import com.qourier.qourier_app.data.Delivery;
import java.util.Base64;
import java.util.List;

import com.qourier.qourier_app.data.Rider;
import com.qourier.qourier_app.data.dto.RiderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private DeliveriesManager deliveriesManager;

    @MockBean private AccountManager accountManager;

    private List<Delivery> deliveryList;
    private List<Delivery> filteredDeliveryList;

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
        when(deliveriesManager.getDeliveryState(any()))
                .thenReturn(deliveryList.get(0).getDeliveryState());
    }

    @Test
    @DisplayName("Obtain all deliveries")
    void whenGetAllDeliveries_thenReturnAllDeliveries() throws Exception {
        MvcResult result =
                mvc.perform(get("/api/v1/deliveries")).andExpect(status().isOk()).andReturn();

        String resultDeliveriesString = result.getResponse().getContentAsString();
        String expectedDeliveries =
                "[{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test0"
                    + " street\",\"originAddr\":\"Test0 origin"
                    + " street\",\"riderId\":null,\"latitude\":10.0,"
                    + "\"longitude\":20.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},{\"customerId\":\"test1@email.com\",\"deliveryAddr\":\"Test1"
                    + " street\",\"originAddr\":\"Test1 origin"
                    + " street\",\"riderId\":null,\"latitude\":11.0,"
                    + "\"longitude\":21.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},{\"customerId\":\"test2@email.com\",\"deliveryAddr\":\"Test2"
                    + " street\",\"originAddr\":\"Test2 origin"
                    + " street\",\"riderId\":null,\"latitude\":12.0,"
                    + "\"longitude\":22.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test3"
                    + " street\",\"originAddr\":\"Test3 origin"
                    + " street\",\"riderId\":null,\"latitude\":13.0,"
                    + "\"longitude\":23.0,\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null}]";
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
                "[{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test0"
                        + " street\",\"originAddr\":\"Test0 origin street\",\"riderId\":null,"
                        + "\"latitude\":10.0,\"longitude\":20.0,"
                        + "\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null},"
                        + "{\"customerId\":\"test0@email.com\",\"deliveryAddr\":\"Test3"
                        + " street\",\"originAddr\":\"Test3 origin street\",\"riderId\":null,"
                        + "\"latitude\":13.0,\"longitude\":23.0,"
                        + "\"deliveryState\":\"BID_CHECK\",\"deliveryId\":null}]";
        assertEquals(expectedDeliveries, resultDeliveriesString);
    }

    @Test
    @DisplayName("Create delivery through post")
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
                                        .param(
                                                "basicAuth",
                                                Base64.getEncoder()
                                                        .encodeToString(
                                                                "test0@email.com".getBytes()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isCreated())
                        .andReturn();
    }

    @Test
    @DisplayName("Try to create delivery through post but fail Auth")
    void whenPostDeliveryWithWrongCreds_thenDeliveryIsntCreated() throws Exception {
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
                                        .param(
                                                "basicAuth",
                                                Base64.getEncoder()
                                                        .encodeToString(
                                                                "wrong@email.com".getBytes()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    @DisplayName("Obtain progress for delivery")
    void whenGetProgress_thenReturnProgress() throws Exception {
        MvcResult result =
                mvc.perform(get("/api/v1/deliveries/progress/0"))
                        .andExpect(status().isOk())
                        .andReturn();

        String resultDeliveriesString = result.getResponse().getContentAsString();
        String expectedDeliveries = "\"BID_CHECK\"";
        assertEquals(expectedDeliveries, resultDeliveriesString);
    }

    @Test
    @DisplayName("Update progress for delivery")
    void whenUpdatingProgress_thenProgressShouldUpdate() throws Exception {
        // Update progress
        mvc.perform(
                        post("/api/v1/deliveries/progress")
                                .param(
                                        "data",
                                        "1",
                                        "rider@email.com",
                                        Base64.getEncoder()
                                                .encodeToString("rider@email.com".getBytes())))
                .andExpect(status().isOk())
                .andReturn();

        verify(deliveriesManager, times(1)).setDeliveryState(1L, "rider@email.com");
    }

    @Test
    @DisplayName("Try to update progress for delivery but fail Auth")
    void whenUpdatingProgressWithWrongCreds_thenProgressShouldntUpdate() throws Exception {
        // Update progress
        mvc.perform(
                        post("/api/v1/deliveries/progress")
                                .param(
                                        "data",
                                        "1",
                                        "rider@email.com",
                                        Base64.getEncoder()
                                                .encodeToString("wrong@email.com".getBytes())))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(deliveriesManager, times(0)).setDeliveryState(1L, "rider@email.com");
    }

    @Test
    @DisplayName("Create bid for delivery")
    void whenCreatingBid_thenProgressShouldUpdate() throws Exception {
        Rider testRider = new Rider(new Account("Rider1","rider@email.com", "password"), "1234567890");

        when(accountManager.getRiderAccount("rider@email.com")).thenReturn(RiderDTO.fromEntity(testRider));

        // Create Bid
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(new Bid("rider@email.com", 1L, null));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/deliveries/bid")
                                        .param(
                                                "basicAuth",
                                                Base64.getEncoder()
                                                        .encodeToString(
                                                                "rider@email.com".getBytes()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isCreated())
                        .andReturn();

        verify(deliveriesManager, times(1)).createBid(any());
    }

    @Test
    @DisplayName("Create bid for delivery")
    void whenCreatingBidWithWorkingJob_thenProgressShouldntUpdate() throws Exception {
        Rider testRider = new Rider(new Account("Rider1","rider@email.com", "password"), "1234567890");

        // Set rider as already working on a  delivery
        testRider.setCurrentDelivery(1L);

        when(accountManager.getRiderAccount("rider@email.com")).thenReturn(RiderDTO.fromEntity(testRider));

        // Create Bid
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(new Bid("rider@email.com", 1L, null));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/deliveries/bid")
                                        .param(
                                                "basicAuth",
                                                Base64.getEncoder()
                                                        .encodeToString(
                                                                "rider@email.com".getBytes()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isForbidden())
                        .andReturn();

        verify(deliveriesManager, times(0)).createBid(any());
    }

    @Test
    @DisplayName("Create bid for delivery but with wrong creds")
    void whenCreatingBidWithWrongCreds_thenProgressShouldntUpdate() throws Exception {
        // Create Bid
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(new Bid("rider@email.com", 1L, null));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/deliveries/bid")
                                        .param(
                                                "basicAuth",
                                                Base64.getEncoder()
                                                        .encodeToString(
                                                                "wrong@email.com".getBytes()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isForbidden())
                        .andReturn();

        verify(deliveriesManager, times(0)).createBid(any());
    }

    @Test
    @DisplayName("Create rider account")
    void whenCreatingRiderAccount_thenAccountAuthTokenShouldBeGiven() throws Exception {
        when(accountManager.registerRider(any())).thenReturn(true);

        // Create Rider account
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(
                        new RiderRegisterRequest(
                                "rider@email.com", "password", "Diego", "134567890"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/accounts/register/rider")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isCreated())
                        .andReturn();
        assertEquals(
                result.getResponse().getContentAsString(),
                Base64.getEncoder().encodeToString("rider@email.com".getBytes()));
    }

    @Test
    @DisplayName("Try to create rider account but already exists")
    void whenCreatingRiderAccountThatExists_thenAccountAuthTokenShouldntBeGiven() throws Exception {
        when(accountManager.registerRider(any())).thenReturn(false);

        // Create Rider account
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(
                        new RiderRegisterRequest(
                                "rider@email.com", "password", "Diego", "134567890"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/accounts/register/rider")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    @DisplayName("Create customer account")
    void whenCreatingCustomerAccount_thenAccountAuthTokenShouldBeGiven() throws Exception {
        when(accountManager.registerCustomer(any())).thenReturn(true);

        // Create Customer Account
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(
                        new CustomerRegisterRequest(
                                "customer@email.com", "password", "notDiego", "Fruits"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/accounts/register/customer")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isCreated())
                        .andReturn();
        assertEquals(
                result.getResponse().getContentAsString(),
                Base64.getEncoder().encodeToString("customer@email.com".getBytes()));
    }

    @Test
    @DisplayName("Try to create customer account but already exists")
    void whenCreatingCustomerAccountThatExists_thenAccountAuthTokenShouldntBeGiven()
            throws Exception {
        when(accountManager.registerCustomer(any())).thenReturn(false);

        // Create Customer Account
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(
                        new CustomerRegisterRequest(
                                "customer@email.com", "password", "notDiego", "Fruits"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/accounts/register/customer")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    @DisplayName("Login to account")
    void whenLogingInWithRightCreds_thenAccountAuthTokenShouldBeGiven() throws Exception {
        when(accountManager.login(any())).thenReturn(LOGGED_IN);

        // Create Customer Account
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(new LoginRequest("account@gmail.com", "password"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/accounts/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isAccepted())
                        .andReturn();

        assertEquals(
                result.getResponse().getContentAsString(),
                Base64.getEncoder().encodeToString("account@gmail.com".getBytes()));
    }

    @Test
    @DisplayName("Try to login to account but with wrong creds")
    void whenLogingInWithWrongCreds_thenAccountAuthTokenShouldntBeGiven() throws Exception {
        when(accountManager.login(any())).thenReturn(WRONG_CREDENTIALS);

        // Create Customer Account
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(new LoginRequest("account@gmail.com", "password"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/accounts/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    @DisplayName("Try to login to account but account doesnt exist")
    void whenLogingInNonExistentAccount_thenAccountAuthTokenShouldntBeGiven() throws Exception {
        when(accountManager.login(any())).thenReturn(NON_EXISTENT_ACCOUNT);

        // Create Customer Account
        ObjectMapper objectMapper = new ObjectMapper();
        String json =
                objectMapper.writeValueAsString(new LoginRequest("account@gmail.com", "password"));

        MvcResult result =
                mvc.perform(
                                post("/api/v1/accounts/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }
}
