/* RETURNS & REVERSE LOGISTICS */

CREATE TABLE returns (
                         return_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         fulfillment_id BIGINT NOT NULL,
                         return_label_uri VARCHAR(512),
                         pickup_window_start DATETIME,
                         pickup_window_end DATETIME,
                         status VARCHAR(40),
                         received_at DATETIME,
                         inspection_result_json TEXT NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

/* PRICING, BILLING & CARRIER SETTLEMENT*/

CREATE TABLE pricing_rule (
                              rule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              conditions_json TEXT,
                              calculation_json TEXT,
                              effective_from DATETIME,
                              effective_to DATETIME,
                              priority INT,
                              status VARCHAR(40),
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE carrier_booking (
                                 carrier_booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 carrier_id BIGINT NOT NULL,
                                 fulfillment_id BIGINT NOT NULL,
                                 external_ref VARCHAR(255),
                                 booked_at DATETIME,
                                 status VARCHAR(40),
                                 fee_amount DECIMAL(12,2),
                                 currency VARCHAR(10),
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE carrier_settlement (
                                    settle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    carrier_id BIGINT NOT NULL,
                                    period_start DATETIME,
                                    period_end DATETIME,
                                    gross_billed DECIMAL(14,2),
                                    carrier_fees DECIMAL(14,2),
                                    commissions DECIMAL(14,2),
                                    net_payable DECIMAL(14,2),
                                    discrepancies_json TEXT,
                                    generated_at DATETIME,
                                    status VARCHAR(40),
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

