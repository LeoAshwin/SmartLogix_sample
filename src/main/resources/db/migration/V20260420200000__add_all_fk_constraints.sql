-- ============================================================
-- SmartLogix: Full FK Constraint Integration
-- Strategy: Fix FK-side column types to match existing PKs,
--           then enforce all referential integrity constraints.
-- ============================================================


-- ============================================================
-- STEP 1: Fix column types on FK-side tables to match PK types
-- ============================================================

-- fulfillment.fulfillment_id = VARCHAR(50)   → all referencing BIGINT cols → VARCHAR(50)
-- drivers.driver_id           = CHAR(36) UUID → all referencing BIGINT cols → CHAR(36)
-- vehicles.vehicle_id         = CHAR(36) UUID → all referencing BIGINT cols → CHAR(36)
-- carriers.carrier_id         = CHAR(36) UUID → all referencing BIGINT cols → CHAR(36)

-- tracking_event.fulfillment_id: BIGINT → VARCHAR(50)
ALTER TABLE tracking_event MODIFY COLUMN fulfillment_id VARCHAR(50) NOT NULL;

-- fulfillment.merchant_id: VARCHAR(50) → BIGINT
ALTER TABLE fulfillment MODIFY COLUMN merchant_id BIGINT;

-- pod.fulfillment_id: BIGINT → VARCHAR(50)
-- pod.delivered_by: BIGINT → CHAR(36)  (references drivers.driver_id)
ALTER TABLE pod MODIFY COLUMN fulfillment_id VARCHAR(50) NOT NULL;
ALTER TABLE pod MODIFY COLUMN delivered_by CHAR(36) NOT NULL;

-- delivery_exception.fulfillment_id: BIGINT → VARCHAR(50)
-- delivery_exception.raised_by: BIGINT → CHAR(36) (references drivers.driver_id)
ALTER TABLE delivery_exception MODIFY COLUMN fulfillment_id VARCHAR(50) NOT NULL;
ALTER TABLE delivery_exception MODIFY COLUMN raised_by CHAR(36) NOT NULL;

-- returns.fulfillment_id: BIGINT → VARCHAR(50)
ALTER TABLE returns MODIFY COLUMN fulfillment_id VARCHAR(50) NOT NULL;

-- carrier_booking.fulfillment_id: BIGINT → VARCHAR(50)
-- carrier_booking.carrier_id: BIGINT → CHAR(36)
ALTER TABLE carrier_booking MODIFY COLUMN fulfillment_id VARCHAR(50) NOT NULL;
ALTER TABLE carrier_booking MODIFY COLUMN carrier_id CHAR(36) NOT NULL;

-- carrier_settlement.carrier_id: BIGINT → CHAR(36)
ALTER TABLE carrier_settlement MODIFY COLUMN carrier_id CHAR(36) NOT NULL;

-- carrier_adapter.carrier_id: BIGINT → CHAR(36)
ALTER TABLE carrier_adapter MODIFY COLUMN carrier_id CHAR(36) NOT NULL;

-- Manifest.VehicleID: BIGINT → CHAR(36) (references vehicles.vehicle_id)
-- Manifest.DriverID:  BIGINT → CHAR(36) (references drivers.driver_id)
ALTER TABLE Manifest MODIFY COLUMN VehicleID CHAR(36);
ALTER TABLE Manifest MODIFY COLUMN DriverID CHAR(36);

-- drivers.user_id: BIGINT stays (references users.user_id which is BIGINT) ✅

-- Add missing column: report.generated_by_fk → references users.user_id
ALTER TABLE report ADD COLUMN generated_by_fk BIGINT;


-- ============================================================
-- STEP 2: Clean up orphan records before adding constraints
-- ============================================================

-- Delete fulfillments with invalid zones
DELETE FROM fulfillment WHERE service_zone_id NOT IN (SELECT zone_id FROM service_zone) AND service_zone_id IS NOT NULL;

-- Delete tracking events for missing fulfillments
DELETE FROM tracking_event WHERE fulfillment_id NOT IN (SELECT fulfillment_id FROM fulfillment);

-- Delete PODs for missing fulfillments or drivers
DELETE FROM pod WHERE fulfillment_id NOT IN (SELECT fulfillment_id FROM fulfillment);
DELETE FROM pod WHERE delivered_by NOT IN (SELECT driver_id FROM drivers);

-- Delete exceptions for missing fulfillments or drivers
DELETE FROM delivery_exception WHERE fulfillment_id NOT IN (SELECT fulfillment_id FROM fulfillment);
DELETE FROM delivery_exception WHERE raised_by NOT IN (SELECT driver_id FROM drivers);

-- Delete returns for missing fulfillments
DELETE FROM returns WHERE fulfillment_id NOT IN (SELECT fulfillment_id FROM fulfillment);

-- Delete bookings for missing carriers or fulfillments
DELETE FROM carrier_booking WHERE carrier_id NOT IN (SELECT carrier_id FROM carriers);
DELETE FROM carrier_booking WHERE fulfillment_id NOT IN (SELECT fulfillment_id FROM fulfillment);

-- Delete settlements for missing carriers
DELETE FROM carrier_settlement WHERE carrier_id NOT IN (SELECT carrier_id FROM carriers);

-- Delete adapters for missing carriers
DELETE FROM carrier_adapter WHERE carrier_id NOT IN (SELECT carrier_id FROM carriers);

-- Delete manifests for missing depots, vehicles, or drivers
DELETE FROM Manifest WHERE DepotID NOT IN (SELECT depot_id FROM depot) AND DepotID IS NOT NULL;
DELETE FROM Manifest WHERE VehicleID NOT IN (SELECT vehicle_id FROM vehicles) AND VehicleID IS NOT NULL;
DELETE FROM Manifest WHERE DriverID NOT IN (SELECT driver_id FROM drivers) AND DriverID IS NOT NULL;

-- Delete legs for missing manifests
DELETE FROM RouteLeg WHERE ManifestID NOT IN (SELECT ManifestID FROM Manifest);

-- Delete drivers for missing users
DELETE FROM drivers WHERE user_id NOT IN (SELECT user_id FROM users) AND user_id IS NOT NULL;

-- Delete reports for missing users
DELETE FROM report WHERE generated_by_fk NOT IN (SELECT user_id FROM users) AND generated_by_fk IS NOT NULL;


-- ============================================================
-- STEP 3: Add all FK constraints
-- ============================================================

-- 1. Fulfillment → ServiceZone (both VARCHAR(50))
ALTER TABLE fulfillment
    ADD CONSTRAINT fk_fulfillment_service_zone
        FOREIGN KEY (service_zone_id) REFERENCES service_zone(zone_id);

-- 1b. Fulfillment → Merchant
ALTER TABLE fulfillment
    ADD CONSTRAINT fk_fulfillment_merchant
        FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id);

-- 2. TrackingEvent → Fulfillment
ALTER TABLE tracking_event
    ADD CONSTRAINT fk_tracking_fulfillment
        FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);

-- 3. Pod → Fulfillment
ALTER TABLE pod
    ADD CONSTRAINT fk_pod_fulfillment
        FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);

-- 4. Pod → Driver (delivered_by)
ALTER TABLE pod
    ADD CONSTRAINT fk_pod_driver
        FOREIGN KEY (delivered_by) REFERENCES drivers(driver_id);

-- 5. DeliveryException → Fulfillment
ALTER TABLE delivery_exception
    ADD CONSTRAINT fk_exception_fulfillment
        FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);

-- 6. DeliveryException → Driver (raised_by)
ALTER TABLE delivery_exception
    ADD CONSTRAINT fk_exception_driver
        FOREIGN KEY (raised_by) REFERENCES drivers(driver_id);

-- 7. Return → Fulfillment
ALTER TABLE returns
    ADD CONSTRAINT fk_return_fulfillment
        FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);

-- 8. CarrierBooking → Carrier
ALTER TABLE carrier_booking
    ADD CONSTRAINT fk_booking_carrier
        FOREIGN KEY (carrier_id) REFERENCES carriers(carrier_id);

-- 9. CarrierBooking → Fulfillment
ALTER TABLE carrier_booking
    ADD CONSTRAINT fk_booking_fulfillment
        FOREIGN KEY (fulfillment_id) REFERENCES fulfillment(fulfillment_id);

-- 10. CarrierSettlement → Carrier
ALTER TABLE carrier_settlement
    ADD CONSTRAINT fk_settlement_carrier
        FOREIGN KEY (carrier_id) REFERENCES carriers(carrier_id);

-- 11. CarrierAdapter → Carrier
ALTER TABLE carrier_adapter
    ADD CONSTRAINT fk_adapter_carrier
        FOREIGN KEY (carrier_id) REFERENCES carriers(carrier_id);

-- 12. Manifest → Depot
ALTER TABLE Manifest
    ADD CONSTRAINT fk_manifest_depot
        FOREIGN KEY (DepotID) REFERENCES depot(depot_id);

-- 13. Manifest → Vehicle
ALTER TABLE Manifest
    ADD CONSTRAINT fk_manifest_vehicle
        FOREIGN KEY (VehicleID) REFERENCES vehicles(vehicle_id);

-- 14. Manifest → Driver
ALTER TABLE Manifest
    ADD CONSTRAINT fk_manifest_driver
        FOREIGN KEY (DriverID) REFERENCES drivers(driver_id);

-- 15. RouteLeg → Manifest
ALTER TABLE RouteLeg
    ADD CONSTRAINT fk_routeleg_manifest
        FOREIGN KEY (ManifestID) REFERENCES Manifest(ManifestID);

-- 16. Driver → User
ALTER TABLE drivers
    ADD CONSTRAINT fk_driver_user
        FOREIGN KEY (user_id) REFERENCES users(user_id);

-- 17. Report → User (generated_by)
ALTER TABLE report
    ADD CONSTRAINT fk_report_user
        FOREIGN KEY (generated_by_fk) REFERENCES users(user_id);
