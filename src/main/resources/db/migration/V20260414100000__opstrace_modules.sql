-- =====================================================
-- OPS TRACE TABLES
-- Modules 4.9 (Reporting, KPIs, Audit Packages)
-- Module 4.10 (Integrations and Channel Adapters)
-- NO FOREIGN KEYS
-- EXACTLY AS PER PDF ENTITIES
-- =====================================================

-- =====================================================
-- AUDIT PACKAGE
-- Entity:
-- AuditPackage(PackageID, PeriodStart, PeriodEnd,
--              ContentsJSON, GeneratedAt, PackageURI)
-- =====================================================
CREATE TABLE audit_package (
                               package_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
                               period_start   DATETIME NOT NULL,
                               period_end     DATETIME NOT NULL,
                               contents_json  JSON,
                               generated_at   DATETIME NOT NULL,
                               package_uri    VARCHAR(255)
);

-- =====================================================
-- KPI
-- Entity:
-- KPI(KPIID, Name, Definition, Target,
--     CurrentValue, ReportingPeriod)
-- =====================================================
CREATE TABLE kpi (
                     kpi_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                     name              VARCHAR(255) NOT NULL,
                     definition        VARCHAR(255),
                     target            DECIMAL(10,2),
                     current_value     DECIMAL(10,2),
                     reporting_period  VARCHAR(100)
);

-- =====================================================
-- REPORT
-- Entity:
-- Report(ReportID, Scope, ParametersJSON,
--        MetricsJSON, GeneratedAt, ReportURI)
-- =====================================================
CREATE TABLE report (
                        report_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                        scope            VARCHAR(255),
                        parameters_json  JSON,
                        metrics_json     JSON,
                        generated_at     DATETIME NOT NULL,
                        report_uri       VARCHAR(255)
);

-- =====================================================
-- CARRIER ADAPTER
-- Entity:
-- CarrierAdapter(AdapterID, CarrierID, Protocol,
--                CredentialsJSON, LastSyncAt, Status)
-- NO FK CONSTRAINT ON carrier_id
-- =====================================================
CREATE TABLE carrier_adapter (
                                 adapter_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 carrier_id        BIGINT NOT NULL,
                                 protocol          VARCHAR(100) NOT NULL,
                                 credentials_json  JSON,
                                 last_sync_at      DATETIME,
                                 status            VARCHAR(50)
);