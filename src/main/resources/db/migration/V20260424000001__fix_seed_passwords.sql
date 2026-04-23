-- Fix the initial seeded users' passwords to use the correct BCrypt hash for "Password@123"
UPDATE users 
SET password_hash = '$2a$12$jyFNardvB9NhLZxcqqOc4.6BcBcdzd0R6V1x.hftG2YSZATe4Fh2y'
WHERE email IN (
    'admin@smartlogix.io',
    'manager@smartlogix.io',
    'dispatcher@smartlogix.io',
    'driver@smartlogix.io',
    'customer@smartlogix.io',
    'merchant@smartlogix.io',
    'carrier@smartlogix.io',
    'finance@smartlogix.io'
);
