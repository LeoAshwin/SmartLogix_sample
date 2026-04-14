package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.PODStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Proof of Delivery â€” captures photo URIs, signature URI, quantity, and notes.
 * Photo/signature files are stored in object storage; only URIs + SHA-256 hashes
 * are stored here for tamper evidence.
 */
@Entity
@Table(name = "proofs_of_delivery", indexes = {
        @Index(name = "idx_pod_fulfillment", columnList = "fulfillment_id", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfDelivery extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fulfillment_id", nullable = false, unique = true)
    private Fulfillment fulfillment;

    @Column(nullable = false)
    private LocalDateTime deliveredAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivered_by", nullable = false)
    private User deliveredBy;

    /**
     * JSON array: [{"uri":"s3://...","sha256":"..."},...]
     */
    @Column(columnDefinition = "TEXT")
    private String photoUrisJson;

    /** Object-storage URI of the signature image */
    private String signatureUri;

    /** SHA-256 checksum of the signature image for tamper evidence */
    private String signatureSha256;

    @Column(nullable = false)
    private Integer quantityDelivered;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PODStatus status;
}

