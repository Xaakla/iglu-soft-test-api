INSERT INTO INGREDIENT(name, sale_price) VALUES
    ('Ingredient A', 10),
    ('Ingredient B', 20),
    ('Ingredient C', 30),
    ('Ingredient D', 40),
    ('Ingredient E', 50);

INSERT INTO DISH(name, total_price) VALUES
    ('Test Dish A', 510),
    ('Test Dish B', 50);

INSERT INTO DISH_INGREDIENT_QUANTITY(dish_id, ingredient_id, quantity) VALUES
    (1, 5, 4),
    (1, 3, 7),
    (1, 2, 5),
    (2, 1, 5);