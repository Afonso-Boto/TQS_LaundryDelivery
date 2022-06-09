package com.qourier.qourier_app.data.dto;

import lombok.Data;

@Data
public class BidDTO {

    private String ridersId;
    private Long deliveryId;
    private Double distance;
}
