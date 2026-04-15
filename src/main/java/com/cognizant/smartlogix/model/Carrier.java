package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.CarrierStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "carriers")
public class Carrier {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "carrier_id", columnDefinition = "char(36)")
    private UUID carrier_id;

    private String name;

    @Column(columnDefinition = "json")
    private String contractTermsJson;

    @Column(columnDefinition = "json")
    private String allowedZonesJson;

    private double maxWeightKg;

    @Enumerated(EnumType.STRING)
    private CarrierStatus status = CarrierStatus.ACTIVE;

    private Instant createdAt = Instant.now();

    // getters and setters

}
