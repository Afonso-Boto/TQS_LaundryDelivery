package tqs.project.laundryplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.project.laundryplatform.model.Laundry;

@Repository
public interface LaundryRepository extends JpaRepository<Laundry, Long> {
    Optional<Laundry> findById(Long laundryId);

    Optional<Laundry> findByName(String name);
}
