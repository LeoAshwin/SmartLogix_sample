package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.DriverStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue
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
