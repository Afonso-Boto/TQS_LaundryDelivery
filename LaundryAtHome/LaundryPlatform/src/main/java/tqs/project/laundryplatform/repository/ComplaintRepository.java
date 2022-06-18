package tqs.project.laundryplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.laundryplatform.model.Complaint;

import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findById(Long complaintId);

    Optional<Complaint> findByTitle(String title);
}
