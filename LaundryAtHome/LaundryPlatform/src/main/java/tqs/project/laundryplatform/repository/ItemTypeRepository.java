package tqs.project.laundryplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.project.laundryplatform.model.ItemType;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {
    Optional<ItemType> findById(long id);

    Optional<ItemType> findByName(String name);
}
