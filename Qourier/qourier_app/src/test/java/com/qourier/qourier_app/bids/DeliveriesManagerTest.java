package com.qourier.qourier_app.bids;

import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.account.register.RiderRegisterRequest;
import com.qourier.qourier_app.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("/application-test.properties")
public class DeliveriesManagerTest {

    @Autowired private DeliveriesManager deliveryManager;
    @Autowired private AccountManager accountManager;
    private final int AuctionSpan = 2;

    @BeforeEach
    public void setUp() {
        deliveryManager.setNewAuctionSpan(AuctionSpan);
    }

    @Test
    void whenDeliveryAuctionIsCreated_ItEndsAndDeletesDeliveryIfNoOneBid() {
        Delivery delivery =
                new Delivery(
                        "test0@email.com", 99.99, 99.99, "test address", "test origin address");

        deliveryManager.createDelivery(delivery);

        try {
            SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        // Wait until auction is finished
        await().atMost(AuctionSpan + 1, SECONDS)
                .untilAsserted(
                        () ->
                                assertThat(deliveryManager.getDelivery(delivery.getDeliveryId()))
                                        .isEqualTo(null));
    }

    @Test
    void whenRiderBidsAndWins_ItsAssignedTheDelivery() {
        // Deliveries
        Delivery delivery =
                new Delivery(
                        "test0@email.com", 99.99, 99.99, "test address", "test origin address");
        deliveryManager.createDelivery(delivery);

        // Riders
        RiderRegisterRequest riderRequest =
                new RiderRegisterRequest("email0@email.com", "password", "rider0", "0123456789");
        accountManager.registerRider(riderRequest);

        // Bids
        Bid bid = new Bid(riderRequest.getEmail(), delivery.getDeliveryId(), 1000.909);
        deliveryManager.createBid(bid);

        // Wait until auction is finished
        await().atMost(AuctionSpan + 1, SECONDS)
                .untilAsserted(
                        () ->
                                assertThat(
                                                accountManager
                                                        .getRiderAccount(riderRequest.getEmail())
                                                        .getCurrentDelivery())
                                        .isEqualTo(delivery.getDeliveryId()));
    }

    @Test
    void whenMultipleRiderBid_ClosestWins() {
        // Deliveries
        Delivery delivery =
                new Delivery(
                        "test0@email.com", 99.99, 99.99, "test address", "test origin address");
        deliveryManager.createDelivery(delivery);

        // Riders
        RiderRegisterRequest riderRequest0 =
                new RiderRegisterRequest("email0@email.com", "password", "rider0", "0123456789");
        accountManager.registerRider(riderRequest0);

        RiderRegisterRequest riderRequest1 =
                new RiderRegisterRequest("email1@email.com", "password", "rider1", "0123456789");
        accountManager.registerRider(riderRequest1);

        // Bids
        Bid bid0 = new Bid(riderRequest0.getEmail(), delivery.getDeliveryId(), 1000.909);
        deliveryManager.createBid(bid0);

        Bid bid1 = new Bid(riderRequest1.getEmail(), delivery.getDeliveryId(), 10.909);
        deliveryManager.createBid(bid1);

        // Wait until auction is finished
        await().atMost(AuctionSpan + 1, SECONDS)
                .untilAsserted(
                        () ->
                                assertThat(
                                                accountManager
                                                        .getRiderAccount(riderRequest1.getEmail())
                                                        .getCurrentDelivery())
                                        .isEqualTo(delivery.getDeliveryId()));
    }

    @Test
    void whenMultipleRiderBid_RiderWhoHasDistanceWins() {
        // Deliveries
        Delivery delivery =
                new Delivery(
                        "test0@email.com", 99.99, 99.99, "test address", "test origin address");
        deliveryManager.createDelivery(delivery);

        // Riders
        RiderRegisterRequest riderRequest0 =
                new RiderRegisterRequest("email0@email.com", "password", "rider0", "0123456789");
        accountManager.registerRider(riderRequest0);

        RiderRegisterRequest riderRequest1 =
                new RiderRegisterRequest("email1@email.com", "password", "rider1", "0123456789");
        accountManager.registerRider(riderRequest1);

        // Bids
        Bid bid0 = new Bid(riderRequest0.getEmail(), delivery.getDeliveryId(), 1000.909);
        deliveryManager.createBid(bid0);

        Bid bid1 = new Bid(riderRequest1.getEmail(), delivery.getDeliveryId(), null);
        deliveryManager.createBid(bid1);

        // Wait until auction is finished
        await().atMost(AuctionSpan + 1, SECONDS)
                .untilAsserted(
                        () ->
                                assertThat(
                                                accountManager
                                                        .getRiderAccount(riderRequest0.getEmail())
                                                        .getCurrentDelivery())
                                        .isEqualTo(delivery.getDeliveryId()));
    }
}
