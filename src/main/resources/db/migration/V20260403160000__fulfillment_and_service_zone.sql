-- ------------------------------------------------------------
-- 1. FULFILLMENT
-- Core order/fulfillment record created during order ingestion
-- ------------------------------------------------------------

CREATE TABLE fulfillment (
                             fulfillment_id        VARCHAR(50) PRIMARY KEY,
                             order_id              VARCHAR(50),
                             merchant_id           VARCHAR(50),
                             service_zone_id       VARCHAR(50),

    -- Stored as STRING to match @Enumerated(EnumType.STRING)
                             service_level         VARCHAR(30),

    -- Weight and volume stored as DECIMAL for precision
                             package_weight_kg     DECIMAL(10,2),
                             package_volume_m3     DECIMAL(10,3),

    -- JSON stored as TEXT for portability
                             dimensions_json       TEXT,

                             delivery_window_start TIMESTAMP,
                             delivery_window_end   TIMESTAMP,

    -- Enum: PENDING, ASSIGNED, EN_ROUTE, DELIVERED, FAILED, RETURNED
                             status                VARCHAR(30),

                             created_at            TIMESTAMP,
                             updated_at            TIMESTAMP
);

-- Index to support high-volume querying by zone and status
CREATE INDEX idx_fulfillment_zone_status
    ON fulfillment(service_zone_id, status);


-- ------------------------------------------------------------
-- 2. SERVICE ZONE
-- Configuration table for delivery zones & SLA rules
-- ------------------------------------------------------------

CREATE TABLE service_zone (
                              zone_id            VARCHAR(50) PRIMARY KEY,
                              name               VARCHAR(100),

    -- Geo boundary stored as GeoJSON
                              polygon_geojson    TEXT,

    -- Postal codes list stored as JSON
                              postal_codes_json  TEXT,

    -- SLA rules stored as JSON
                              sla_config_json    TEXT,

                              capacity_per_slot  INT,
                              time_zone          VARCHAR(50),

    -- Enum: ACTIVE, INACTIVE
                              status             VARCHAR(30)
);


-- ------------------------------------------------------------
-- OPTIONAL FUTURE CONSTRAINTS (INTENTIONALLY COMMENTED)
-- Logical relationships handled at application layer
-- ------------------------------------------------------------

-- -- LINKING FULFILLMENT TO SERVICE ZONE
-- ALTER TABLE fulfillment
-- ADD CONSTRAINT fk_fulfillment_service_zone
-- FOREIGN KEY (service_zone_id) REFERENCES service_zone(zone_id);