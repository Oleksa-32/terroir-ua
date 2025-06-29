-- Reset sequence first
ALTER TABLE cart_items ALTER COLUMN id RESTART WITH 1;

-- Insert cart items with proper amounts
INSERT INTO cart_items (id, cart_id, wine_id, quantity, is_deleted)
VALUES
    (1, 1, 1, 2, false),  -- 2 bottles of Wine One (25.99 each)
    (2, 1, 2, 3, false);  -- 3 bottles of Wine Two (19.99 each)