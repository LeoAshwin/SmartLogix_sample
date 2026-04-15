package com.cognizant.smartlogix.model;
import com.cognizant.smartlogix.model.data.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "delivery_exception")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exceptionId;

    @Column(nullable = false)
    private Long fulfillmentId;

    private LocalDateTime raisedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Long raisedBy;

    @Column(nullable = false)
    private String reasonCode;

    @Column(columnDefinition = "TEXT")
    private String details;

    private String suggestedAction;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status= DeliveryStatus.OPEN;

    private Integer retryCount = 0;
}