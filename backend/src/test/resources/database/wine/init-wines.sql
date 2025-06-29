DELETE FROM wines;
ALTER TABLE wines ALTER COLUMN id RESTART WITH 1;

INSERT INTO wines (
    id, name, wine_year, wine_type, price, producer,
    description, owner_description, rate,
    aging_method, sweetness, region, variety,
    percentage, date_added, volume, image_url, is_deleted
) VALUES
      (1, 'Wine One', 2020, 'RED', 25.99, 'Producer A', 'Description', 'Owner description',
       4.5, 'Oak', 'Dry', 'Bordeaux', 'Cabernet Sauvignon', 13.5, '2023-06-24 10:00:00',
       750, '/images/wine1.jpg', false),
      (2, 'Wine Two', 2018, 'WHITE', 19.99, 'Producer B', 'Description', 'Owner description',
       4.2, 'Steel', 'Medium', 'Burgundy', 'Chardonnay', 12.5, '2023-06-20 11:00:00',
       750, '/images/wine2.jpg', false),
      (3, 'Wine Three', 2015, 'RED', 45.99, 'Producer C', 'Description', 'Owner description',
       4.8, 'Oak', 'Dry', 'Tuscany', 'Sangiovese', 14.5, '2023-06-15 09:00:00',
       750, '/images/wine3.jpg', false);

ALTER TABLE wines ALTER COLUMN id RESTART WITH 4;