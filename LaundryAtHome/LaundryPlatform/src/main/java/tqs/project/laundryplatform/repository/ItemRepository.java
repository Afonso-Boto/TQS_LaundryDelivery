package tqs.project.laundryplatform.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.laundryplatform.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long id);
}
