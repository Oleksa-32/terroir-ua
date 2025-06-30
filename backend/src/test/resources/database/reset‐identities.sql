ALTER TABLE cart_items      ALTER COLUMN id RESTART WITH 3;

-- users: add-cart.sql created user id=1, so next = 2
ALTER TABLE users           ALTER COLUMN id RESTART WITH 2;

-- wines: init-wines.sql inserted wine ids 1–5, so next = 6
ALTER TABLE wines           ALTER COLUMN id RESTART WITH 6;