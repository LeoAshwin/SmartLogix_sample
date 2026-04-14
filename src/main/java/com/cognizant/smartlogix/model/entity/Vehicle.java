package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.VehicleStatus;
import com.cognizant.smartlogix.model.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Vehicle in the fleet â€” owned or contracted.
 * fleetId can reference an external fleet management system.
 */
@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicles_reg_no", columnList = "registrationNumber", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    /** External fleet system ID, nullable for owned vehicles */
    private UUID fleetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal capacityKg;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal capacityVolumeM3;

    @Column(nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;
}

