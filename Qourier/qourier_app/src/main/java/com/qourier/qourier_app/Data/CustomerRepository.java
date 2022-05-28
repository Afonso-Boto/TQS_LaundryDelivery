package com.qourier.qourier_app.Data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository   extends JpaRepository<Customer, Long> {
    Customer findByName(String name);

}
