package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.dto.Driver.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.TrackingMetadata;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.cognizant.smartlogix.model.data.EventType;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;


@Entity
@Table(name = "tracking_event")
@Data
@Builder
//Hibernate needs the empty constructor to "reconstitute" objects from the database
//while the Builder needs the all-args constructor to create them
@AllArgsConstructor
@NoArgsConstructor
public class TrackingEvent {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // automatically generates values (Id's)
    private Long eventId;

    @Column(nullable = false)
    private Long fulfillmentId;

    // Use an Enum for EventType [Accepted/Assigned/OutForDelivery/Delivered/Failed/Returned]
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp = LocalDateTime.now();

    // Use your DTO here instead of a Map
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private LocationDetails locationJson; // Stores {lat, lng}

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private TrackingMetadata detailsJson;// Stores device info/offline sync metadata

}