package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    Rider findByAccount_Email(String email);
    List<Rider> findByAccount_Name(String name);

}
