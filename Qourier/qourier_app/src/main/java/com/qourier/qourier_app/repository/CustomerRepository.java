package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.AccountState;
import com.qourier.qourier_app.data.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Customer findByAccount_Email(String email);
    List<Customer> findByAccount_Name(String name);
    Page<Customer> findByAccount_StateIn(Collection<AccountState> states, Pageable pageable);

}
