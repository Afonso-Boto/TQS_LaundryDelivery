package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.bids.DeliveriesManager;
import com.qourier.qourier_app.data.Bid;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.DeliveryState;
import com.qourier.qourier_app.data.dto.BidDTO;
import com.qourier.qourier_app.data.dto.DeliveryDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class ApiController {

    private final DeliveriesManager deliveriesManager;

    @Autowired
    public ApiController(DeliveriesManager deliveriesManager) {
        this.deliveriesManager = deliveriesManager;
    }

    @GetMapping("/deliveries")
    public List<Delivery> deliveriesGet(
            @RequestParam(defaultValue = "", required = false, name = "customerId")
                    String customerId) {

        // Check if filter or not
        if (!customerId.equals("")) return deliveriesManager.getDeliveriesFromCustomer(customerId);

        return deliveriesManager.getAllDeliveries();
    }

    @PostMapping("/deliveries")
    public ResponseEntity<Delivery> deliveriesPost(@RequestBody DeliveryDTO newDelivery) {
        Delivery delivery = deliveriesManager.createDelivery(Delivery.fromDto(newDelivery));
        return new ResponseEntity<>(delivery, HttpStatus.CREATED);
    }

    @GetMapping("/deliveries/progress/{deliveryId}")
    public DeliveryState deliveriesProgressGet(@PathVariable String deliveryId) {
        return deliveriesManager.getDeliveryState(Long.valueOf(deliveryId));
    }

    @PostMapping("/deliveries/progress")
    public HttpStatus deliveryProgressPost(@RequestParam List<String> data) {
        // Get data from params
        String deliveryId = data.get(0);
        String riderId = data.get(1);

        deliveriesManager.setDeliveryState(Long.valueOf(deliveryId), riderId);

        return HttpStatus.OK;
    }

    @PostMapping("/deliveries/bid")
    public ResponseEntity<Bid> deliveriesBidPost(@RequestBody BidDTO newBid) {
        Bid bid = deliveriesManager.createBid(Bid.fromDto(newBid));
        return new ResponseEntity<>(bid, HttpStatus.CREATED);
    }
}
