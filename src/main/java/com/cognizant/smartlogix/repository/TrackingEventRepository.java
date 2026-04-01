package com.cognizant.smartlogix.repository;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {

    // Find all events for a specific fulfillment, ordered by time for the history view
    List<TrackingEvent> findByFulfillmentIdOrderByEventTimestampDesc(Long fulfillmentId);

    // Get the very latest status of a package (The "Current Status" logic)
    Optional<TrackingEvent> findFirstByFulfillmentIdOrderByEventTimestampDesc(Long fulfillmentId);

    // Count events by type - useful for Harshine's KPI module (Module 4.9)
    long countByEventType(EventType eventType);


}