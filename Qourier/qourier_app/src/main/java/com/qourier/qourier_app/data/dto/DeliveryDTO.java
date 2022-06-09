package com.qourier.qourier_app.data.dto;

import com.qourier.qourier_app.data.DeliveryState;
import lombok.Data;

@Data
public class DeliveryDTO {

    private String customerId, deliveryAddr, originAddr, riderId;
    private double latitude, longitude;
    private DeliveryState deliveryState;
}
