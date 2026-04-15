package com.cognizant.smartlogix.repository;
import com.cognizant.smartlogix.model.Pod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PodRepository extends JpaRepository<Pod, Long> {

    Optional<Pod> findByFulfillmentId(Long fulfillmentId);

    boolean existsByFulfillmentId(Long fulfillmentId);

}