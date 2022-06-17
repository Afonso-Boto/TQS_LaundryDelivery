package tqs.project.laundryplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.laundryplatform.model.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}
