package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Driver / Courier profile linked to a platform User.
 */
@Entity
@Table(name = "drivers", indexes = {
        @Index(name = "idx_drivers_license", columnList = "licenseNumber", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(length = 20)
    private String phone;

    /**
     * JSON: { "monday": {"start":"08:00","end":"20:00"}, "tuesday": {...} }
     */
    @Column(columnDefinition = "TEXT")
    private String shiftScheduleJson;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal maxDailyHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DriverStatus status;
}

