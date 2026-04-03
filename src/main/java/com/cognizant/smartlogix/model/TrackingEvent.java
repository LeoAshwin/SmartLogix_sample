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
@AllArgsConstructor
@NoArgsConstructor
public class TrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false)
    private Long fulfillmentId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp = LocalDateTime.now();


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private LocationDetails locationJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private TrackingMetadata detailsJson;

}