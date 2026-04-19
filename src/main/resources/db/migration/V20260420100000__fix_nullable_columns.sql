-- Fix nullable constraint violations uncovered by E2E integration testing
-- The following columns were NOT NULL in original migrations but are not always set by service layer

ALTER TABLE returns MODIFY COLUMN inspection_result_json TEXT NULL;
