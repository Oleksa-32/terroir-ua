INSERT INTO cart_items (id, cart_id, wine_id, quantity, is_deleted)
SELECT COALESCE(MAX(id), 0) + 1, 1, 3, 1, false FROM cart_items;