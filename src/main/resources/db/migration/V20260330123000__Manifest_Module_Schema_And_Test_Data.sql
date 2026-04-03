-- V20260330153000__Manifest_Module_Standalone_NoFK.sql

-- DROP TABLE IF EXISTS RouteLeg;
-- DROP TABLE IF EXISTS Manifest;

CREATE TABLE if not exists Manifest (
                          ManifestID BIGINT PRIMARY KEY AUTO_INCREMENT,
                          DepotID BIGINT,             -- Changed to BIGINT to match Java Long
                          VehicleID BIGINT,           -- Changed to BIGINT to match Java Long
                          DriverID BIGINT,            -- Changed to BIGINT to match Java Long
                          Date DATE,
                          StartAt DATETIME,
                          EndAt DATETIME,
                          StopsJSON JSON,             -- Stores summary list
                          Status VARCHAR(20) DEFAULT 'GENERATED'
);

CREATE TABLE if not exists RouteLeg (
                          LegID BIGINT PRIMARY KEY AUTO_INCREMENT,
                          ManifestID BIGINT,          -- Linked to Manifest
                          Sequence INT,
                          FromLocationJSON JSON,      -- Stores {"lat": 12.3, "lng": 80.1}
                          ToLocationJSON JSON,        -- Stores {"lat": 12.4, "lng": 80.2}
                          DistanceKm DECIMAL(10, 2),
                          EstimatedDurationMinutes INT,
                          Status VARCHAR(20) DEFAULT 'PENDING'
);