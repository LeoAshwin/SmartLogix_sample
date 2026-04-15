package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.DriverStatus;
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
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "driver_id", columnDefinition = "char(36)")
    private UUID driverId;

    private String licenseNumber;
    private String phone;

    @Column(columnDefinition = "json")
    private String shiftScheduleJson;

    private int maxDailyHours;

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.ACTIVE;

    private Instant createdAt = Instant.now();

    // getters and setters

}
