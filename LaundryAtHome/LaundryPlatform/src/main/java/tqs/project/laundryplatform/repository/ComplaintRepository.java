package tqs.project.laundryplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.laundryplatform.model.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findById(Long complaintId);

    Optional<Complaint> findByTitle(String title);
}
