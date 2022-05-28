package com.qourier.qourier_app.Data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository  extends JpaRepository<Person, Long> {
    Person findByName(String name);
    List<Person> findAll();

}
