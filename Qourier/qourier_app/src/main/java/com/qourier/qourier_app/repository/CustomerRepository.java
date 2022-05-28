package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByAccount_Email(String email);
    List<Customer> findByAccount_Name(String name);

}
