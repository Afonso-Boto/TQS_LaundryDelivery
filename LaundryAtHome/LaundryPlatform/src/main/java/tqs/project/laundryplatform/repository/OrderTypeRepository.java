package tqs.project.laundryplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.project.laundryplatform.model.OrderType;

import java.util.Optional;

@Repository
public interface OrderTypeRepository extends JpaRepository<OrderType, Long> {

    Optional<OrderType> findById(long id);

}
