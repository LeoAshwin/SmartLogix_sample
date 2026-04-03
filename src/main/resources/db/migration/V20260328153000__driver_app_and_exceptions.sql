CREATE TABLE tracking_event (
                                event_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                fulfillment_id BIGINT NOT NULL,
                                event_type VARCHAR(50) NOT NULL,
                                location_json JSON,
                                details_json JSON,
                                event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                INDEX idx_tracking_fulfillment (fulfillment_id, event_timestamp)
);


CREATE TABLE pod (
                     pod_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     fulfillment_id BIGINT NOT NULL UNIQUE,
                     delivered_at TIMESTAMP NULL,
                     delivered_by BIGINT NOT NULL,
                     photo_uris_json JSON,
                     signature_uri VARCHAR(555),
                     quantity_delivered INT DEFAULT 1,
                     notes TEXT,
                     status VARCHAR(20) DEFAULT 'PENDING',
                     checksum_sha256 VARCHAR(64)
);


CREATE TABLE delivery_exception (
                                    exception_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    fulfillment_id BIGINT NOT NULL,
                                    raised_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    raised_by BIGINT NOT NULL,
                                    reason_code VARCHAR(50) NOT NULL,
                                    details TEXT,
                                    suggested_action VARCHAR(255),
                                    retry_count INT DEFAULT 0,
                                    status VARCHAR(20) DEFAULT 'OPEN'
);














-- -- 1. LINKING TRACKING EVENTS (Kamalesh) TO FULFILLMENT (Harini)
-- ALTER TABLE tracking_event
--     ADD CONSTRAINT fk_tracking_fulfillment
--         FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);
--
-- -- 2. LINKING POD (Kamalesh) TO FULFILLMENT (Harini)
-- ALTER TABLE pod
--     ADD CONSTRAINT fk_pod_fulfillment
--         FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);
--
-- -- 3. LINKING POD (Kamalesh) TO DRIVER REGISTRY (Amila)
-- -- Ensures the 'delivered_by' person is a valid registered driver
-- ALTER TABLE pod
--     ADD CONSTRAINT fk_pod_delivered_by
--         FOREIGN KEY (delivered_by_fk) REFERENCES driver(driver_id);
--
-- -- 4. LINKING EXCEPTIONS (Kamalesh) TO FULFILLMENT (Harini)
-- ALTER TABLE delivery_exception
--     ADD CONSTRAINT fk_exception_fulfillment
--         FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);
--
-- -- 5. LINKING EXCEPTIONS (Kamalesh) TO DRIVER REGISTRY (Amila)
-- -- Ensures the 'raised_by' person is a valid registered driver
-- ALTER TABLE delivery_exception
--     ADD CONSTRAINT fk_exception_raised_by
--         FOREIGN KEY (raised_by_fk) REFERENCES driver(driver_id);