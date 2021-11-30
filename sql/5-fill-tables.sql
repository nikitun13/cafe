INSERT INTO users (email, password, role, first_name, last_name, phone, points, is_blocked)
VALUES ('bad-user@mail.ru', '$2a$10$PN4zI00T8FpBamfts18eYeyA8oAn..bvMKLhVB6LVBvZDBEcyVJ7a',
        'CLIENT', 'Петр', 'Петров', '375441553281', 0, false), --password: qwerty123
       ('good-user@gmail.com', '$2a$10$PN4zI00T8FpBamfts18eYeyA8oAn..bvMKLhVB6LVBvZDBEcyVJ7a',
        'CLIENT', 'Ivan', 'Ivanov', '375442341231', 0, false); --password: qwerty123

INSERT INTO dish (name, category, price, description)
VALUES ('Chicken BBQ', 'PIZZA', 2890, 'Pizza with onions, chicken, BBQ sauce, bacon, champignons, mozzarella cheese'),
       ('Five cheeses', 'PIZZA', 3240,
        'Pizza with feta, cream fresh, blue cheese, parmesan, cheddar, mozzarella cheese'),
       ('Pepperoni', 'PIZZA', 3140, 'Pizza with tomato sauce, mozzarella cheese, pepperoni'),
       ('Farm pizza', 'PIZZA', 2640, 'Pizza with cucumbers, ham, mozzarella cheese, garlic sauce'),
       ('Mexican', 'PIZZA', 2870,
        'Pizza with mozzarella cheese, bell pepper, burger sauce, jalapeno, chicken, corn, tomatoes'),
       ('Munich', 'PIZZA', 3440,
        'Pizza with bavarian sausages, tomatoes, BBQ sauce, mustard, mozzarella cheese, munich sausages, ham'),
       ('Chicken Ranch', 'PIZZA', 2640, 'Pizza with garlic sauce, tomatoes, mozzarella cheese, chicken'),
       ('Carbonara', 'PIZZA', 2890, 'Pizza with champignons, onions, cream fresh, bacon, mozzarella cheese, ham'),
       ('Provence', 'PIZZA', 2850, 'Pizza with tomatoes, blue cheese, mozzarella cheese, cream fresh, pepperoni, ham'),
       ('Riviera', 'PIZZA', 3260, 'Pizza with garlic sauce, spinach, mozzarella cheese, cherry, ham, olives'),
       ('Country', 'PIZZA', 3210,
        'Pizza with garlic sauce, onion, ham, mozzarella cheese, cucumbers, champignons, bacon'),
       ('Pepperoni Blues', 'PIZZA', 2550, 'Pizza with blue cheese, pepperoni, mozzarella cheese, garlic sauce'),
       ('Fanta', 'DRINKS', 280, 'Fanta is the soft drink that intensifies fun'),
       ('Coca-Cola Zero', 'DRINKS', 280,
        'Coca‑Cola Zero is our sugar free cola, that looks and tastes even more like Coca‑Cola original taste, but without the sugar'),
       ('Coca-Cola', 'DRINKS', 280, 'Original Coca‑Cola'),
       ('Sprite', 'DRINKS', 280, 'Sprite is a colorless, lemon and lime-flavored soft drink'),
       ('Greek', 'SALADS', 750,
        'Salad with tomatoes, fresh cucumbers, sweet onions, bell peppers, olive oil, salt, pepper, lettuce'),
       ('Caesar', 'SALADS', 790,
        'Salad with green salad, tomatoes, chicken fillet, white bread, Caesar sauce, butter, garlic, Parmesan cheese'),
       ('Potato wedges', 'SNACKS', 500, 'Potato wedges with seasoning'),
       ('Barbecue', 'SAUCES', 80, 'Barbecue sauce'),
       ('Tomato', 'SAUCES', 80, 'Tomato sauce'),
       ('Garlic', 'SAUCES', 80, 'Garlic sauce'),
       ('Caesar', 'SAUCES', 80, 'Caesar sauce'),
       ('Cheese', 'SAUCES', 80, 'Cheese sauce'),
       ('Sweet and sour sauce', 'SAUCES', 80, 'Sweet and sour sauce');

INSERT INTO comment (user_id, dish_id, rating, body, created_at)
VALUES (1, 1, 5,
        'Мне нравится это кафе - как-то так получилось, что пиццу теперь только тут и покупаю, так как случайно заглянув туда однажды мне все понравилось и пока ни разу (тьфутьфу) разочарований не было. Из плюсов сытный перекус, хорошее тесто, не пересушена, из минусов, наверное, маловато начинки и многовато соуса, учитывая его яркий и специфический вкус, внешний вид в целом',
        '2021.11.15 20:30:19'),
       (2, 1, 3,
        'Возвращаясь с дочерью домой к обеду, мы решили разнообразить рацион)))Зашли в Магнит и купили эту пиццу. Упаковка выглядит цивильно. Указанный вес 420 гр. Но кажется она значительно легче. Весов нет, не смогла проверить,увы. Грамм 300 с хвостиком... И сама пицца очень даже достойная , если дома совсем нечего есть! Хотя ветчины и грибочков можно было бы положить побольше... Сыра достаточно, но вкус странноватый, довольно пресный. Корж не могу назвать очень вкусным, но точно не отвратительный, как многие говорят. Вкусы , видимо, у всех разные. Мне не хватило остроты и я на свой кусочек намазывала шашлычный кетчуп дополнительно) Получилось вообще ОГОНЬ! Так что если не смотреть на цену и не придираться, то можно сказать, что сама по себе вкусная пицца! Начинки много(но хотелось бы больше)!',
        '2021.11.16 11:23:03'),
       (3, 1, 1,
        'Самая вкусная часть пиццы - это была хрустящая корочка, в остальном какой-то провал',
        '2021.11.16 15:17:23'),
       (2, 1, 4,
        'Сыра и сливочного соуса в пицце много, они чувствуются. Пицца мягкая, корочка хрустящая, начинка тоже вся разнотекстурная, что и добавляет удовольствия трапезе.Несмотря на наличие сладких составляющих в пицце, она, конечно, никакая не десертная. Это блюдо, которое лучше было бы употребить на завтрак, чем на ужин. Пицца маленького размера, я бы смогла осилить ее в одиночку (моя же, как всегда, была разделена на двоих, этого мало, но больше потому, что пицца очень вкусная и хочется ее больше). Пиццу начинали есть теплой, когда заканчивали есть, она остыла но во всех температурах хороша. Это действительно удачная и оригинальная новинка, мне они угодили. Ингредиенты подобраны гармонично, соуса в достатке, травы тоже подошли по вкусу. Пицца - не самое полезное блюдо, но легло нормально, без тяжести (потому что пицца была небольшая)).',
        '2021.11.13 19:34:56'),
       (1, 1, 5,
        'Highly recommended pizza in this cafe. Super looovee your pizza.. 😍😍 Must try Angus beef, Aloha and the Cheesiest. Winner po. 🙂 Thank you again. 🥰🥰',
        '2021.11.17 10:13:43'),
       (3, 1, 5,
        'i ordered via messenger. They are prompt to answer. They make comfortable arrangement to meet demands. Pizza is great.i ordered the Cheesy trio saver ..flavors are all equally good. Delivered on time and still hot .will certainly again. Highly recommendable ..thank u',
        '2021.11.17 11:18:23'),
       (2, 1, 2,
        'Have tried a few different kinds here. The crust is decent, but not crisp or chewy, salty of yeasty-if you like either that way but was similar to a plain pita. The topping were sparse, very little cheese, the sauce was so light and had no flavor at all. It was by consensus of my entire group that this was by far the blandest pizza any of us had ever eaten.',
        '2021.11.18 08:08:28'),
       (2, 13, 2,
        'Я такие напитки покупаю редко, по причине дороговизны по сравнению с другими газированными напитками. Хоть и вторые тоже не часто покупаю, но по цене предпочитаю более дешевые. Недостатки: вызывает зависимость в том плане ,что сколько не пей, хочется еще. И дорого. Достоинства: вкусно.',
        '2021.11.18 10:18:58');

INSERT INTO orders (user_id, created_at, expected_retrieve_date, actual_retrieve_date, status, debited_points,
                    accrued_points, total_price)
VALUES ((SELECT id FROM users WHERE email = 'good-user@gmail.com'), '2021.11.20 10:18:58', '2021.11.21 10:20:00',
        '2021.11.21 10:15:13', 'COMPLETED', 0, 163, 3250),
       ((SELECT id FROM users WHERE email = 'good-user@gmail.com'), '2021.11.25 15:15:19', '2021.11.27 13:00:00',
        '2021.11.27 13:01:10', 'COMPLETED', 100, 218, 4360),
       ((SELECT id FROM users WHERE email = 'bad-user@mail.ru'), '2021.11.27 11:00:12', '2021.11.27 12:10:00',
        NULL, 'NOT_COLLECTED', 0, 0, 12160);


INSERT INTO dish_orders (order_id, dish_id, dish_price, dish_count)
VALUES ((SELECT id FROM orders WHERE created_at = '2021.11.20 10:18:58'), 1, 2890, 1),
       ((SELECT id FROM orders WHERE created_at = '2021.11.20 10:18:58'), 20, 80, 1),
       ((SELECT id FROM orders WHERE created_at = '2021.11.20 10:18:58'), 13, 280, 1),
       ((SELECT id FROM orders WHERE created_at = '2021.11.25 15:15:19'), 11, 3210, 1),
       ((SELECT id FROM orders WHERE created_at = '2021.11.25 15:15:19'), 19, 500, 1),
       ((SELECT id FROM orders WHERE created_at = '2021.11.25 15:15:19'), 17, 750, 1),
       ((SELECT id FROM orders WHERE created_at = '2021.11.27 11:00:12'), 1, 2890, 2),
       ((SELECT id FROM orders WHERE created_at = '2021.11.27 11:00:12'), 2, 3240, 1),
       ((SELECT id FROM orders WHERE created_at = '2021.11.27 11:00:12'), 3, 3140, 1);
