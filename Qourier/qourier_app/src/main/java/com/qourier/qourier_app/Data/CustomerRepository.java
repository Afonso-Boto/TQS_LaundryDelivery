package com.qourier.qourier_app.Data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository   extends JpaRepository<Customer, Long> {
    Customer findByName(String name);
    List<Customer> findAll();

}
