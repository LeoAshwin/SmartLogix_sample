package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    private String action;

    private String resourceType;

    private String resourceId;

    @Column(columnDefinition = "json")
    private String detailsJson;

    private LocalDateTime timestamp = LocalDateTime.now();

}
