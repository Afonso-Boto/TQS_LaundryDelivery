package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Bid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidsRepository extends JpaRepository<Bid, String> {
    Bid findByRidersId(String ridersId);

    List<Bid> findByDeliveryId(Long deliveryId, Sort sort);

    List<Bid> findByDeliveryId(Long deliveryId);
}
