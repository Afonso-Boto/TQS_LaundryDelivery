package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Delivery;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    List<Delivery> findByCustomerId(String customerId);

    Delivery findByDeliveryId(Long deliveryId);

    List<Delivery> findAll();
}
