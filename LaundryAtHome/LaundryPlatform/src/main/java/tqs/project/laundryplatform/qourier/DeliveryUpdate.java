package tqs.project.laundryplatform.qourier;

import lombok.Data;

@Data
public class DeliveryUpdate {
    private long deliveryId;
    private String state;
}
