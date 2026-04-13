package com.cognizant.smartlogix.model.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "audit_package")
@Getter
@Setter
public class AuditPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "period_start")
    private String periodStart;

    @Column(name = "period_end")
    private String periodEnd;

    @Column(name = "contents_json", columnDefinition = "TEXT")
    private String contentsJson;

    @Column(name = "generated_at")
    private String generatedAt;

    @Column(name = "package_uri")
    private String packageUri;
}
