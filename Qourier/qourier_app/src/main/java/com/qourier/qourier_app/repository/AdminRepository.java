package com.qourier.qourier_app.repository;

import com.qourier.qourier_app.data.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByAccount_Email(String email);
    List<Admin> findByAccount_Name(String name);

}
