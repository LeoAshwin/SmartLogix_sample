package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "carrier_adapter")
@Getter
@Setter
public class CarrierAdapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adapter_id")
    private Long adapterId;

    @Column(name = "carrier_id", nullable = false)
    private String carrierId;


    @Column(name = "protocol", nullable = false)
    private String protocol; // REST | FTP | CUSTOM

    @Column(name = "credentials_json", columnDefinition = "json")
    private String credentialsJson;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "status")
    private String status; // SANDBOX | ACTIVE | DISABLED
}