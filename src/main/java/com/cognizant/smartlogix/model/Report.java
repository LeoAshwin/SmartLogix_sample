package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "scope")
    private String scope;

    @Column(name = "parameters_json", columnDefinition = "json")
    private String parametersJson;

    @Column(name = "metrics_json", columnDefinition = "json")
    private String metricsJson;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "report_uri")
    private String reportUri;

    @Column(name = "generated_by_fk")
    private Long generatedByFk;

    public Long getGeneratedByFk() { return generatedByFk; }
    public void setGeneratedByFk(Long generatedByFk) { this.generatedByFk = generatedByFk; }
}