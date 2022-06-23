package tqs.project.laundryplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.project.laundryplatform.model.OrderType;

@Repository
public interface OrderTypeRepository extends JpaRepository<OrderType, Long> {

    Optional<OrderType> findById(long id);

    Optional<OrderType> findByName(String name);
}
