package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_package")
@Getter
@Setter
public class AuditPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    @Column(columnDefinition = "json")
    private String contentsJson;

    private LocalDateTime generatedAt;
    private String packageUri;
}
