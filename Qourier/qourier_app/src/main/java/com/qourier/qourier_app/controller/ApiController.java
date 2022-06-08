package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.bids.DeliveriesManager;
import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.DeliveryState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qourier.qourier_app.data.DeliveryState.*;

@RestController
@RequestMapping("/api/v1/")
public class ApiController {

    private final DeliveriesManager deliveriesManager;

    @Autowired
    public ApiController(DeliveriesManager deliveriesManager){ this.deliveriesManager = deliveriesManager; }

    @GetMapping("/deliveries")
    public List<Delivery> deliveriesGet(@RequestParam(defaultValue = "", required = false, name = "customerId") String customerId){

        // Check if filter or not
        if(!customerId.equals(""))
            return deliveriesManager.getDeliveriesFromCustomer(customerId);

        return deliveriesManager.getAllDeliveries();
    }

    @PostMapping("/deliveries")
    public ResponseEntity<Delivery> deliveriesPost(@RequestBody Delivery newDelivery){
        Delivery delivery = deliveriesManager.createDelivery(newDelivery);
        return new ResponseEntity<>(delivery, HttpStatus.CREATED);
    }


    @GetMapping("/deliveries/progress/{delivery_id}")
    public DeliveryState deliveriesProgressGet(@PathVariable String delivery_id){
        return deliveriesManager.getDeliveryState(Long.valueOf(delivery_id));
    }


    @PostMapping("/deliveries/progress")
    public void deliveryProgressPost(@RequestBody String delivery_id, @RequestBody String rider_id){
        Delivery delivery = deliveriesManager.getDelivery(Long.valueOf(delivery_id));
        DeliveryState previousState = delivery.getDeliveryState();

        // Iterate states if rider id is right
        if( delivery.getRiderId().equals(rider_id)){
            switch (previousState) {
                case BID_CHECK -> delivery.setDeliveryState(FETCHING);
                case FETCHING -> delivery.setDeliveryState(SHIPPED);
                case SHIPPED -> delivery.setDeliveryState(DELIVERED);
                case DELIVERED -> delivery.setDeliveryState(BID_CHECK);
            }
        }
    }
}
