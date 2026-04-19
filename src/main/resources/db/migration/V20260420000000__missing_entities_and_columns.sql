-- Adding missing columns to existing tables
ALTER TABLE vehicles ADD COLUMN fleet_id BIGINT;
ALTER TABLE drivers ADD COLUMN user_id BIGINT;

-- Missing Domains creation

CREATE TABLE IF NOT EXISTS depot (
    depot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    address_json JSON,
    time_zone VARCHAR(100),
    capacity_json JSON,
    status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS merchant (
    merchant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    contact_info_json JSON,
    billing_terms_json JSON,
    status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    role VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(50),
    password_hash VARCHAR(255),
    mfa_enabled BOOLEAN,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(255),
    resource_type VARCHAR(255),
    resource_id VARCHAR(255),
    details_json JSON,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
