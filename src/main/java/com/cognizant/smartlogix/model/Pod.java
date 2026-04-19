package com.cognizant.smartlogix.model;
import com.cognizant.smartlogix.model.data.PodStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pod")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long podId;

    @Column(unique = true, nullable = false)
    private String fulfillmentId;


    private LocalDateTime deliveredAt;

    @Column(nullable = false)
    private String deliveredBy;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> photoUrisJson;

    private String signatureUri;

    @Builder.Default
    private Integer quantityDelivered=1;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 64)
    private String checksumSha256;

    @Enumerated(EnumType.STRING)
    private PodStatus status = PodStatus.PENDING;
}