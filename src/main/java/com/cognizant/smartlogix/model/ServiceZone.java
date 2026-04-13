package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.model.data.ServiceZoneStatus;
import jakarta.persistence.*;

/**
 * Entity representing a service zone.
 * A service zone defines the geographical service area,
 * SLA configurations, and delivery capacity constraints.
 * Module: 4.2 – Service Zones, SLA & Capacity Management
 */
@Entity
@Table(name = "service_zone")
public class ServiceZone {

    @Id
    @Column(name = "zone_id")
    private String zoneId;

    @Column(name = "name")
    private String name;

    @Column(name = "polygon_geojson", columnDefinition = "TEXT")
    private String polygonGeoJson;

    @Column(name = "postal_codes_json", columnDefinition = "TEXT")
    private String postalCodesJson;

    @Column(name = "sla_config_json", columnDefinition = "TEXT")
    private String slaConfigJson;

    @Column(name = "capacity_per_slot")
    private Integer capacityPerSlot;

    @Column(name = "time_zone")
    private String timeZone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceZoneStatus status;

    // ===== Getters and Setters =====

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPolygonGeoJson() {
        return polygonGeoJson;
    }

    public void setPolygonGeoJson(String polygonGeoJson) {
        this.polygonGeoJson = polygonGeoJson;
    }

    public String getPostalCodesJson() {
        return postalCodesJson;
    }

    public void setPostalCodesJson(String postalCodesJson) {
        this.postalCodesJson = postalCodesJson;
    }

    public String getSlaConfigJson() {
        return slaConfigJson;
    }

    public void setSlaConfigJson(String slaConfigJson) {
        this.slaConfigJson = slaConfigJson;
    }

    public Integer getCapacityPerSlot() {
        return capacityPerSlot;
    }

    public void setCapacityPerSlot(Integer capacityPerSlot) {
        this.capacityPerSlot = capacityPerSlot;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public ServiceZoneStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceZoneStatus status) {
        this.status = status;
    }
}