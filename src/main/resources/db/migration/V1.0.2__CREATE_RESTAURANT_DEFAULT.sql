INSERT INTO restaurants (owner_id, name, cuisine_type, street, number, complement, city, state, zip_code)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Bella Italia', 'Italiana', 'Rua das Flores', '123', 'Lote 4', 'São Paulo', 'SP', '01234-567'),
    ('11111111-1111-1111-1111-111111111111', 'Burger Tech', 'Hamburgueria', 'Av. Paulista', '1000', 'Andar 1', 'São Paulo', 'SP', '01310-100');

INSERT INTO menu_items (restaurant_id, name, description, price, photo_path, restaurant_only)
VALUES
    (1, 'Pizza Margherita', 'Molho de tomate, muçarela e manjericão fresco.', 45.90, '/images/pizza_marg.jpg', false),
    (1, 'Lasanha à Bolonhesa', 'Massa artesanal com ragu de carne bovina e molho bechamel.', 52.00, '/images/lasanha.jpg', false),
    (2, 'Tech Burger Extra', 'Pão brioche, 2 blends de 180g, queijo cheddar e bacon.', 38.50, '/images/tech_burger.jpg', false),
    (2, 'Batata Rústica', 'Porção de batatas com alecrim e páprica.', 18.00, '/images/fries.jpg', true);

INSERT INTO opening_hours (restaurant_id, day_of_week, open_hour, close_hour)
VALUES
-- Bella Italia (Segunda a Sexta)
(1, 'MONDAY', '18:00:00', '23:00:00'),
(1, 'TUESDAY', '18:00:00', '23:00:00'),
(1, 'WEDNESDAY', '18:00:00', '23:00:00'),
(1, 'THURSDAY', '18:00:00', '23:00:00'),
(1, 'FRIDAY', '18:00:00', '23:59:00'),
-- Burger Tech (Final de semana inteiro)
(2, 'SATURDAY', '11:00:00', '23:59:00'),
(2, 'SUNDAY', '11:00:00', '22:00:00');
