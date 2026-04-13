package com.cognizant.smartlogix.model.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "report")
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "generated_at")
    private String generatedAt;

    @Column(name = "report_uri")
    private String reportUri;
}