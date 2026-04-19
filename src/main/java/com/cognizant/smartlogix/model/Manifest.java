package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Manifest")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Manifest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`ManifestID`")
    private Long manifestId;

    @Column(name = "`DepotID`")
    private Long depotId;

    @Column(name = "`VehicleID`")
    private Long vehicleId;

    @Column(name = "`DriverID`")
    private Long driverId;

    @Column(name = "`Date`")
    private LocalDate date;

    @Column(name = "`StartAt`")
    private LocalDateTime startAt;

    @Column(name = "`EndAt`")
    private LocalDateTime endAt;

    @Column(name = "`StopsJSON`", columnDefinition = "JSON")
    private String stopsJson;

    @Column(name = "`Status`")
    private String status;
}