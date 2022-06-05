package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    List<Delivery> findByCustomerId(String customerId);

    Delivery findByDeliveryId(Long deliveryId);

    List<Delivery> findAll();
}
