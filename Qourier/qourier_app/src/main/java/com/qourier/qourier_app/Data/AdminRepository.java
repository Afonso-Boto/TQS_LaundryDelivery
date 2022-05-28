package com.qourier.qourier_app.Data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository   extends JpaRepository<Admin, Long> {
    Admin findByName(String name);
    List<Admin> findAll();

}
