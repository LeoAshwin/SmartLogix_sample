package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchantId;

    private String name;

    @Column(columnDefinition = "json")
    private String contactInfoJson;

    @Column(columnDefinition = "json")
    private String billingTermsJson;

    private String status;

}
