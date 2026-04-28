INSERT INTO users (name, email, phone_number, address_line1, address_line2, address_line3, address_town, address_county, address_postcode) VALUES
('Leandro Dal Bo', 'leandro@test.com', '+441234567890', '123 Main St', NULL, NULL, 'London', 'Greater London', 'E1 6AN'),
('John Smith', 'john.smith@test.com', '+441112223334', '456 High St', 'Apt 2', NULL, 'Manchester', 'Greater Manchester', 'M1 2AB'),
('Deleting User', 'deleteme@test.com', '+441112223333', '777 High St', 'Apt 555', NULL, 'Manchester', 'Greater Manchester', 'M1 444');

-- Accounts
INSERT INTO accounts (user_id, account_number, balance, currency) VALUES
(1, 'ACC-1001', 1500.00, 'GBP'),
(1, 'ACC-1002', 250.50, 'GBP'),
(2, 'ACC-2001', 999.99, 'GBP');

-- Transactions
INSERT INTO transactions (account_id, type, amount, description) VALUES
(1, 'CREDIT', 1000.00, 'Initial deposit'),
(1, 'DEBIT', 50.00, 'Coffee shop'),
(2, 'CREDIT', 250.50, 'Refund'),
(3, 'CREDIT', 999.99, 'Salary payment');