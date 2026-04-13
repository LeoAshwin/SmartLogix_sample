package com.cognizant.smartlogix.model.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "carrier_adapter")
@Getter
@Setter
public class CarrierAdapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adapter_id")
    private Long adapterId;

    @Column(name = "carrier_id")
    private Long carrierId;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "credentials_json", columnDefinition = "TEXT")
    private String credentialsJson;

    @Column(name = "status")
    private String status;

    // ✅ Dummy sandbox flag
    @Column(name = "sandbox_enabled")
    private Boolean sandboxEnabled = true;

    // ✅ Dummy sync timestamp
    @Column(name = "last_sync_at")
    private String lastSyncAt;
}
