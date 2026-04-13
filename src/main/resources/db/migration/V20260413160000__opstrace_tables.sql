-- =====================================================
-- OPS TRACE TABLES (Modules 4.9 & 4.10)
-- =====================================================

-- AUDIT PACKAGE
CREATE TABLE audit_package (
                               package_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
                               period_start   VARCHAR(255),
                               period_end     VARCHAR(255),
                               contents_json  TEXT,
                               generated_at   VARCHAR(255),
                               package_uri    VARCHAR(255)
);

-- KPI
CREATE TABLE kpi (
                     kpi_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                     name         VARCHAR(255),
                     value        VARCHAR(255),
                     unit         VARCHAR(50),
                     recorded_at  VARCHAR(255)
);

-- REPORT
CREATE TABLE report (
                        report_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
                        report_type   VARCHAR(255),
                        generated_at  VARCHAR(255),
                        report_uri    VARCHAR(255)
);

-- CARRIER ADAPTER
CREATE TABLE carrier_adapter (
                                 adapter_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 carrier_id        BIGINT,
                                 protocol          VARCHAR(100),
                                 credentials_json  TEXT,
                                 status            VARCHAR(50),
                                 sandbox_enabled   BOOLEAN DEFAULT TRUE,
                                 last_sync_at      VARCHAR(255)
);
