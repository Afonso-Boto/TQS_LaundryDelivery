package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.data.Delivery;
import com.qourier.qourier_app.data.DeliveryState;
import com.qourier.qourier_app.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.List;

import static com.qourier.qourier_app.data.DeliveryState.*;

@RestController
@RequestMapping("/api/v1/")
public class ApiController {
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public ApiController(DeliveryRepository deliveryRepository){
        this.deliveryRepository = deliveryRepository;
    }

    @GetMapping("/deliveries")
    public List<Delivery> deliveriesGet(@RequestParam(defaultValue = "", required = false, name = "customerId") String consumer_id){
        List<Delivery> result;

        // Check if filter or not
        if(!consumer_id.equals(""))
            return deliveryRepository.findByCustomerId(consumer_id);

        return deliveryRepository.findAll();
    }

    @PostMapping("/deliveries")
    public ResponseEntity<Delivery> deliveriesPost(@RequestBody Delivery newDelivery){
        Delivery delivery = deliveryRepository.save(newDelivery);
        return new ResponseEntity<>(delivery, HttpStatus.CREATED);
    }


    @GetMapping("/deliveries/progress/{delivery_id}")
    public DeliveryState deliveriesProgressGet(@PathVariable String delivery_id){
        return deliveryRepository.findByDeliveryId(delivery_id).getDeliveryState();
    }


    @PostMapping("/deliveries/progress")
    public void deliveryProgressPost(@RequestBody String delivery_id, @RequestBody String rider_id){
        Delivery delivery = deliveryRepository.findByDeliveryId(delivery_id);
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
