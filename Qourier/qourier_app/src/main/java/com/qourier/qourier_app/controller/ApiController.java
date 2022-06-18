package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.account.login.LoginRequest;
import com.qourier.qourier_app.account.login.LoginResult;
import com.qourier.qourier_app.account.register.CustomerRegisterRequest;
import com.qourier.qourier_app.account.register.RiderRegisterRequest;
import com.qourier.qourier_app.bids.DeliveriesManager;
import com.qourier.qourier_app.data.Bid;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.DeliveryState;
import com.qourier.qourier_app.data.dto.BidDTO;
import com.qourier.qourier_app.data.dto.DeliveryDTO;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class ApiController {

    private final DeliveriesManager deliveriesManager;
    private final AccountManager accountManager;

    @Autowired
    public ApiController(DeliveriesManager deliveriesManager, AccountManager accountManager) {
        this.deliveriesManager = deliveriesManager;
        this.accountManager = accountManager;
    }

    @GetMapping("/deliveries")
    public List<Delivery> deliveriesGet(
            @RequestParam(defaultValue = "", required = false, name = "customerId")
                    String customerId,
            @RequestParam(defaultValue = "", required = false, name = "id") String deliveryId) {

        if (!deliveryId.isEmpty())
            return List.of(deliveriesManager.getDelivery(Long.parseLong(deliveryId)));

        // Check if filter or not
        if (!customerId.isEmpty())
            return deliveriesManager.getDeliveriesFromCustomer(customerId);

        return deliveriesManager.getAllDeliveries();
    }

    @PostMapping("/deliveries")
    public ResponseEntity<Delivery> deliveriesPost(
            @RequestBody DeliveryDTO newDelivery, @RequestParam String basicAuth) {
        String customerId = newDelivery.getCustomerId();

        // Check if auth is right
        if (basicAuth.equals(Base64.getEncoder().encodeToString(customerId.getBytes()))) {
            Delivery delivery = deliveriesManager.createDelivery(Delivery.fromDto(newDelivery));
            return new ResponseEntity<>(delivery, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/deliveries/progress/{deliveryId}")
    public DeliveryState deliveriesProgressGet(@PathVariable String deliveryId) {
        return deliveriesManager.getDeliveryState(Long.valueOf(deliveryId));
    }

    @PostMapping("/deliveries/progress")
    public ResponseEntity<Object> deliveryProgressPost(@RequestParam List<String> data) {
        // Get data from params
        String deliveryId = data.get(0);
        String riderId = data.get(1);
        String basicAuth = data.get(2);

        // Check if auth is right
        if (basicAuth.equals(Base64.getEncoder().encodeToString(riderId.getBytes()))) {
            deliveriesManager.setDeliveryState(Long.valueOf(deliveryId), riderId);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/deliveries/bid")
    public ResponseEntity<Bid> deliveriesBidPost(
            @RequestBody BidDTO newBid, @RequestParam String basicAuth) {
        String riderId = newBid.getRidersId();

        // Check if auth is right
        if (basicAuth.equals(Base64.getEncoder().encodeToString(riderId.getBytes())) && accountManager.getRiderAccount(riderId).getCurrentDelivery() == null) {
            Bid bid = deliveriesManager.createBid(Bid.fromDto(newBid));
            return new ResponseEntity<>(bid, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/accounts/login")
    public ResponseEntity<String> accountsLoginPost(@RequestBody LoginRequest request) {
        LoginResult result = accountManager.login(request);
        if (result.equals(LoginResult.WRONG_CREDENTIALS)
                || result.equals(LoginResult.NON_EXISTENT_ACCOUNT))
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        return new ResponseEntity<String>(
                Base64.getEncoder().encodeToString(request.getEmail().getBytes()),
                HttpStatus.ACCEPTED);
    }

    @PostMapping("/accounts/register/rider")
    public ResponseEntity<String> accountsRegisterRiderPost(
            @RequestBody RiderRegisterRequest request) {
        if (accountManager.registerRider(request))
            return new ResponseEntity<String>(
                    Base64.getEncoder().encodeToString(request.getEmail().getBytes()),
                    HttpStatus.CREATED);

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/accounts/register/customer")
    public ResponseEntity<String> accountsRegisterCustomerPost(
            @RequestBody CustomerRegisterRequest request) {
        if (accountManager.registerCustomer(request))
            return new ResponseEntity<String>(
                    Base64.getEncoder().encodeToString(request.getEmail().getBytes()),
                    HttpStatus.CREATED);

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
}
