-- Seed data for E2E integration verification
-- Provides records with specific IDs used in the E2EIntegrationFlowTest

-- 1. Depot
INSERT INTO depot (depot_id, name, status) 
SELECT 1, 'Main Depot', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM depot WHERE depot_id = 1);

-- 2. Vehicle
INSERT INTO vehicles (vehicle_id, type, capacity_kg, registration_number, status)
SELECT 'V-001', 'VAN', 1000.0, 'REG-001', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE vehicle_id = 'V-001');

-- 3. Driver
INSERT INTO drivers (driver_id, license_number, status)
SELECT 'D-001', 'LIC-001', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM drivers WHERE driver_id = 'D-001');

-- 4. Carrier
INSERT INTO carriers (carrier_id, name, status)
SELECT 'C-001', 'Global Logistics', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM carriers WHERE carrier_id = 'C-001');

-- 5. User (Reference for drivers and reports if needed)
INSERT INTO users (user_id, name, status)
SELECT 1, 'Test Admin', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = 1);

-- 6. Merchant
INSERT INTO merchant (merchant_id, name, status)
SELECT 1, 'Test Merchant', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE merchant_id = 1);

-- Link Driver to User
UPDATE drivers SET user_id = 1 WHERE driver_id = 'D-001' AND user_id IS NULL;
