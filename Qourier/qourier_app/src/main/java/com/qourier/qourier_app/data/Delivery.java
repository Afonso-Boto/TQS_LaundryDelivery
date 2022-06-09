package com.qourier.qourier_app.data;

import javax.persistence.*;

import com.qourier.qourier_app.data.dto.DeliveryDTO;
import lombok.Getter;
import lombok.Setter;

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

    public static Delivery fromDto(DeliveryDTO deliveryDTO) {
        return new Delivery(
                deliveryDTO.getCustomerId(),
                deliveryDTO.getLatitude(),
                deliveryDTO.getLongitude(),
                deliveryDTO.getDeliveryAddr(),
                deliveryDTO.getOriginAddr()
        );
    }
}
