package tqs.project.laundryplatform.qourier;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeliveryCreation {
    private String customerId, deliveryAddr, originAddr, riderId, deliveryState;
    private double latitude, longitude;
    private int deliveryId;
    private String creationTime;

    public DeliveryCreation() {
    }

    public DeliveryCreation(String customerId, String deliveryAddr, String originAddr, String riderId, String deliveryState, double latitude, double longitude, int deliveryId, String creationTime) {
        this.customerId = customerId;
        this.deliveryAddr = deliveryAddr;
        this.originAddr = originAddr;
        this.riderId = riderId;
        this.deliveryState = deliveryState;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryId = deliveryId;
        this.creationTime = creationTime;
    }

}
