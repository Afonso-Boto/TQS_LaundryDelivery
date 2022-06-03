package com.qourier.qourier_app.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "delivery")
public class Delivery {
    private String customerId, deliveryAddr, originAddr, riderId;
    private double latitude, longitude;
    private DeliveryState deliveryState;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long deliveryId;

    public Delivery(
            String customerId,
            double latitude,
            double longitude,
            String deliveryAddr,
            String originAddr) {
        this.customerId = customerId;
        this.riderId = null;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryAddr = deliveryAddr;
        this.originAddr = originAddr;
        this.deliveryState = DeliveryState.BID_CHECK;
    }

    public Delivery() {}
}
