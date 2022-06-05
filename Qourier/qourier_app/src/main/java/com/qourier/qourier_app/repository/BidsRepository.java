package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Bid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidsRepository extends JpaRepository<Bid, String> {
    Bid findByRidersId(String ridersId);

    List<Bid> findByDeliveryId(Long deliveryId, Sort sort);

    List<Bid> findByDeliveryId(Long deliveryId);
}
