package com.qourier.qourier_app.bids;

import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.account.register.RiderRegisterRequest;
import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.Bid;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.Rider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("/application-test.properties")
public class DeliveriesManagerTest {

    @Autowired private DeliveriesManager deliveryManager;
    @Autowired private AccountManager accountManager;

    @BeforeEach
    public void setUp() {}

    @Test
    void whenDeliveryAuctionIsCreated_ItEndsAndDeletesDeliveryIfNoOneBid() {
        Delivery delivery =
                new Delivery(
                        "test0@email.com", 99.99, 99.99, "test address", "test origin address");

        deliveryManager.createDelivery(delivery);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        assertThat(deliveryManager.getDelivery(delivery.getDeliveryId())).isEqualTo(null);
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

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        assertThat(accountManager.getRiderAccount(riderRequest.getEmail()).getCurrentDelivery())
                .isEqualTo(delivery.getDeliveryId());
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

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        assertThat(accountManager.getRiderAccount(riderRequest0.getEmail()).getCurrentDelivery())
                .isEqualTo(delivery.getDeliveryId());
    }
}
