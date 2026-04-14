package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.CarrierAdapterProtocol;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Integration adapter for a carrier â€” stores protocol config and credentials.
 * credentialsJson must be encrypted at rest.
 */
@Entity
@Table(name = "carrier_adapters")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierAdapter extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CarrierAdapterProtocol protocol;

    /**
     * Encrypted JSON: { "apiKey": "...", "baseUrl": "...", "ftpHost": "..." }
     * Store encrypted; never log this field.
     */
    @Column(columnDefinition = "TEXT")
    private String credentialsJson;

    private LocalDateTime lastSyncAt;

    @Column(nullable = false, length = 20)
    private String status;

    /** If true, all calls use sandbox endpoints */
    @Column(nullable = false)
    @Builder.Default
    private Boolean sandboxMode = false;
}

