INSERT INTO users (name, email) VALUES
('Leandro Dal Bo', 'leandro@test.com'),
('John Smith', 'john.smith@test.com');

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