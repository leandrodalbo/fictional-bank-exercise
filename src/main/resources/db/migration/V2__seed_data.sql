INSERT INTO users (name, email, phone_number, address_line1, address_line2, address_line3, address_town, address_county, address_postcode) VALUES
('Leandro Dal Bo', 'leandro@test.com', '+441234567890', '123 Main St', NULL, NULL, 'London', 'Greater London', 'E1 6AN'),
('John Smith', 'john.smith@test.com', '+441112223334', '456 High St', 'Apt 2', NULL, 'Manchester', 'Greater Manchester', 'M1 2AB'),
('Deleting User', 'deleteme@test.com', '+441112223333', '777 High St', 'Apt 555', NULL, 'Manchester', 'Greater Manchester', 'M1 444');


INSERT INTO accounts (
    user_id,
    account_number,
    sort_code,
    account_name,
    account_type,
    balance,
    currency,
    created_at,
    updated_at
) VALUES
(1, '01000001', '10-10-11', 'Personal Account', 'personal', 1500.00, 'GBP', NOW(), NOW()),
(1, '01000002', '10-11-11', 'Savings Account', 'personal', 250.50, 'GBP', NOW(), NOW()),
(2, '01000003', '11-11-11', 'Business Account', 'other', 999.99, 'GBP', NOW(), NOW());

-- Transactions
INSERT INTO transactions (account_id, type, amount, currency, reference) VALUES
(1, 'deposit', 1000.00, 'GBP', 'Initial deposit'),
(1, 'withdrawal', 50.00, 'GBP', 'Coffee shop'),
(2, 'deposit', 250.50, 'GBP', 'Refund'),
(3, 'deposit', 999.99, 'GBP', 'Salary payment');