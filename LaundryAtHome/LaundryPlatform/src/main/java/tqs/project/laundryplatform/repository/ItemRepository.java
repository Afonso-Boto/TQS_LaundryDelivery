package tqs.project.laundryplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.laundryplatform.model.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long id);
}
