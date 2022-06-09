package com.qourier.qourier_app.data;

import javax.persistence.*;

import com.qourier.qourier_app.data.dto.BidDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bids")
public class Bid {
    @Id private String ridersId;

    private Long deliveryId;

    private Double distance;

    public static Bid fromDto(BidDTO bidDTO) {
        return new Bid(
                bidDTO.getRidersId(),
                bidDTO.getDeliveryId(),
                bidDTO.getDistance()
        );
    }
}
