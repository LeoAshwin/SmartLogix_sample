
CREATE TABLE carriers (
                          carrier_id CHAR(36) PRIMARY KEY,         -- UUID stored as string (36 chars)
                          name VARCHAR(255) NOT NULL,
                          contract_terms_json JSON,                -- MySQL supports JSON type
                          allowed_zones_json JSON,
                          max_weight_kg DOUBLE,
                          status VARCHAR(50) NOT NULL,             -- Enum stored as string
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Driver table

CREATE TABLE drivers (
                         driver_id CHAR(36) PRIMARY KEY,          -- UUID stored as string (36 chars)
                         license_number VARCHAR(100) NOT NULL,
                         phone VARCHAR(20),
                         shift_schedule_json JSON,                -- MySQL supports JSON type
                         max_daily_hours INT,
                         status VARCHAR(50) NOT NULL,             -- Enum stored as string
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE vehicles (
                          vehicle_id CHAR(36) PRIMARY KEY,          -- UUID stored as string (36 chars)
                          type VARCHAR(100) NOT NULL,               -- Enum VehicleType stored as string
                          capacity_kg DOUBLE,
                          capacity_volume_m3 DOUBLE,
                          registration_number VARCHAR(100) NOT NULL UNIQUE,
                          status VARCHAR(50) NOT NULL,              -- Enum VehicleStatus stored as string
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
