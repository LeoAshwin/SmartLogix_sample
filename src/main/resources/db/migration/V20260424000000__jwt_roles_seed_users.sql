-- =============================================================================
-- SmartLogix JWT Security Migration
-- Seeds one BCrypt-hashed user for each of the 8 RBAC roles.
-- All seed users use password: "Password@123"
-- BCrypt hash generated with strength 12.
-- =============================================================================

-- Ensure the role column is wide enough for enum string values
ALTER TABLE users MODIFY COLUMN role VARCHAR(50);

-- Ensure email column has a unique index (idempotent)
ALTER TABLE users MODIFY COLUMN email VARCHAR(255);

-- Drop duplicate email index if it already exists, then re-create
-- (MySQL will error if you ADD a UNIQUE index that already exists)
SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME   = 'users'
       AND INDEX_NAME   = 'uq_users_email') > 0,
    'SELECT 1',
    'ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =============================================================================
-- Seed Users  (password = "Password@123"  →  BCrypt cost-12 hash)
-- =============================================================================

-- 1. Admin
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'System Admin', 'ADMIN', 'admin@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@smartlogix.io');

-- 2. Logistics Manager
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'Logistics Manager', 'LOGISTICS_MANAGER', 'manager@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'manager@smartlogix.io');

-- 3. Dispatcher
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'Dispatcher', 'DISPATCHER', 'dispatcher@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'dispatcher@smartlogix.io');

-- 4. Driver
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'Driver One', 'DRIVER', 'driver@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'driver@smartlogix.io');

-- 5. Customer
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'Customer One', 'CUSTOMER', 'customer@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer@smartlogix.io');

-- 6. Merchant
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'Merchant One', 'MERCHANT', 'merchant@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'merchant@smartlogix.io');

-- 7. Carrier / 3PL
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'Carrier 3PL', 'CARRIER', 'carrier@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carrier@smartlogix.io');

-- 8. Finance Officer
INSERT INTO users (name, role, email, password_hash, status, mfa_enabled)
SELECT 'Finance Officer', 'FINANCE_OFFICER', 'finance@smartlogix.io',
       '$2a$12$tFirOvFELWiOGQrmzqH0hOJwzN9DWpGxTf7kPJGp4NFPX1lMuLuXq',
       'ACTIVE', false
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'finance@smartlogix.io');
