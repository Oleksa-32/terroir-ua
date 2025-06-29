ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE shopping_carts ALTER COLUMN user_id RESTART WITH 1;

-- Insert test user
INSERT INTO users (id, email, password, name, is_deleted)
VALUES (1, 'user@gmail.com', '$2a$10$NxW3cyRxP33QWbEeAUu2b.QSShHLyYHKtUHrkG5vyISuZzLXksMTa', 'Test User', false);

-- Insert shopping cart for the user
INSERT INTO shopping_carts (user_id, amount, delivery_price, total_price, is_deleted)
VALUES (1, 111.95, 10.00, 121.95, false);