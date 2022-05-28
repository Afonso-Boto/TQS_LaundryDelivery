package com.qourier.qourier_app.Data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiderRepository   extends JpaRepository<Rider, Long> {
    Rider findByName(String name);
    List<Rider> findAll();

}
