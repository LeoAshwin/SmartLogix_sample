package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.TrackingEvent;
import com.cognizant.smartlogix.model.enums.TrackingEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, UUID> {
    List<TrackingEvent> findByFulfillmentIdOrderByTimestampAsc(UUID fulfillmentId);
    Page<TrackingEvent> findByFulfillmentId(UUID fulfillmentId, Pageable pageable);
    List<TrackingEvent> findByFulfillmentIdAndEventType(UUID fulfillmentId, TrackingEventType type);
    List<TrackingEvent> findByFulfillmentIdAndTimestampBetween(
            UUID fulfillmentId, LocalDateTime from, LocalDateTime to);
}

