package tqs.project.laundryplatform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(long id);

    List<Order> findAllByUser(User user);
}