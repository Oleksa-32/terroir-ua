SET REFERENTIAL_INTEGRITY FALSE;

-- 2) truncate & reset only the identity tables
TRUNCATE TABLE cart_items;
ALTER TABLE cart_items ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE wines;
ALTER TABLE wines ALTER COLUMN id RESTART WITH 1;

-- 3) just truncate shopping_carts (no identity to reset)
TRUNCATE TABLE shopping_carts;

-- 4) re-enable FKs
SET REFERENTIAL_INTEGRITY TRUE;