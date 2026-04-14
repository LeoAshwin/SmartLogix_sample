package com.cognizant.smartlogix.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Merchant / Seller entity â€” submits orders, views fulfillment status,
 * and reconciles settlements.
 */
@Entity
@Table(name = "merchants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Merchant extends BaseEntity {

    @Column(nullable = false)
    private String name;

    /** JSON: { "email": "...", "phone": "...", "address": "..." } */
    @Column(columnDefinition = "TEXT")
    private String contactInfoJson;

    /** JSON: { "paymentTermsDays": 30, "invoiceCycle": "MONTHLY" } */
    @Column(columnDefinition = "TEXT")
    private String billingTermsJson;

    @Column(nullable = false, length = 20)
    private String status;
}

