package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "depot")
public class Depot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depotId;

    private String name;

    @Column(columnDefinition = "json")
    private String addressJson;

    private String timeZone;

    @Column(columnDefinition = "json")
    private String capacityJson;

    private String status;

}
