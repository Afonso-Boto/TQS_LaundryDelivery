package tqs.project.laundryplatform.qourier;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Delivery {

    private String customerId, deliveryAddr, originAddr;
    private double latitude, longitude;

    public Delivery(
            String customerId,
            double latitude,
            double longitude,
            String deliveryAddr,
            String originAddr) {
        this.customerId = customerId;
        this.deliveryAddr = deliveryAddr;
        this.originAddr = originAddr;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
