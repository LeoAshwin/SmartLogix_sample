package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.VehicleStatus;
import com.cognizant.smartlogix.model.data.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue
    private UUID vehicleId;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    private double capacityKg;
    private double capacityVolumeM3;

    @Column(unique = true)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status = VehicleStatus.ACTIVE;

    private Instant createdAt = Instant.now();

    // getters and setters

}
