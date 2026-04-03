package com.cognizant.smartlogix.repository;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {


    List<TrackingEvent> findByFulfillmentIdOrderByEventTimestampDesc(Long fulfillmentId);


    Optional<TrackingEvent> findFirstByFulfillmentIdOrderByEventTimestampDesc(Long fulfillmentId);


    long countByEventType(EventType eventType);


}