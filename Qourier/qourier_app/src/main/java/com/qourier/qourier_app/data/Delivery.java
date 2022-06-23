package com.qourier.qourier_app.data;

import com.qourier.qourier_app.data.dto.DeliveryDTO;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "delivery")
public class Delivery {
    private String customerId;
    private String deliveryAddr;
    private String originAddr;
    private String riderId;
    private double latitude;
    private double longitude;
    private DeliveryState deliveryState;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long deliveryId;

    private LocalDateTime creationTime;

    public Delivery(
            String customerId,
            double latitude,
            double longitude,
            String deliveryAddr,
            String originAddr) {
        this();
        this.customerId = customerId;
        this.riderId = null;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryAddr = deliveryAddr;
        this.originAddr = originAddr;
        this.deliveryState = DeliveryState.BID_CHECK;
    }

    public Delivery() {
        creationTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public static Delivery fromDto(DeliveryDTO deliveryDTO) {
        return new Delivery(
                deliveryDTO.getCustomerId(),
                deliveryDTO.getLatitude(),
                deliveryDTO.getLongitude(),
                deliveryDTO.getDeliveryAddr(),
                deliveryDTO.getOriginAddr());
    }
}
