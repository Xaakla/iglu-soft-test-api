INSERT INTO INGREDIENT(name, sale_price) VALUES
    ('Alface', 40),
    ('Bacon', 200),
    ('Hamburguer de carne', 300),
    ('Ovo', 80),
    ('Queijo', 150);

INSERT INTO DISH(name, total_price) VALUES
    ('X-Bacon', 650),
    ('X-Burguer', 450),
    ('X-Egg', 530),
    ('X-Egg Bacon', 730);

INSERT INTO DISH_INGREDIENT_QUANTITY(dish_id, ingredient_id, quantity) VALUES
    (1, 2, 1),
    (1, 3, 1),
    (1, 5, 1),
    (2, 3, 1),
    (2, 5, 1),
    (3, 4, 1),
    (3, 2, 1),
    (3, 3, 1),
    (3, 5, 1),
    (4, 4, 1),
    (4, 2, 1),
    (4, 3, 1),
    (4, 5, 1);

INSERT INTO OFFER(name, discount_type, discount_amount) VALUES
    ('Light', 'DISH_TOTAL_PRICE_PERCENTAGE_DISCOUNT', 10),
    ('Muita carne', 'INGREDIENT_QUANTITY_DISCOUNT', 0),
    ('Muito queijo', 'INGREDIENT_QUANTITY_DISCOUNT', 0);

INSERT INTO OFFER_INGREDIENT_MIN_QUANTITY(offer_id, ingredient_id, min_quantity, paid_quantity) VALUES
    (1, 1, 1, 1),
    (1, 2, 1, 1),
    (2, 3, 3, 2),
    (3, 5, 3, 2)
;

INSERT INTO OFFER_EXCLUDED_INGREDIENTS(offer_id, excluded_ingredients_id) VALUES
    (1, 2);

INSERT INTO OFFER_REQUIRED_INGREDIENTS(offer_id, required_ingredients_id) VALUES
    (1, 1),
    (2, 3),
    (3, 4)
;