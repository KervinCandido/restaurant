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

-- Criando os usuários
INSERT INTO users (uuid)
VALUES
    ('22222222-2222-2222-2222-222222222222'), -- Funcionário 1
    ('33333333-3333-3333-3333-333333333333'); -- Funcionário 2

-- Atribuindo papéis (roles) básicos para que eles possam trabalhar no sistema
INSERT INTO user_roles (user_uuid, role_name)
VALUES
    ('22222222-2222-2222-2222-222222222222', 'VIEW_RESTAURANT'),
    ('22222222-2222-2222-2222-222222222222', 'VIEW_RESTAURANT_MANAGEMENT'),
    ('22222222-2222-2222-2222-222222222222', 'UPDATE_MENU_ITEM'),
    ('22222222-2222-2222-2222-222222222222', 'VIEW_MENU_ITEM'),
    ('33333333-3333-3333-3333-333333333333', 'VIEW_RESTAURANT'),
    ('33333333-3333-3333-3333-333333333333', 'VIEW_RESTAURANT_MANAGEMENT'),
    ('33333333-3333-3333-3333-333333333333', 'UPDATE_MENU_ITEM'),
    ('33333333-3333-3333-3333-333333333333', 'VIEW_MENU_ITEM');

INSERT INTO restaurant_employees (restaurant_id, employee_uuid)
VALUES
    (1, '22222222-2222-2222-2222-222222222222'), -- Funcionário 1 no Bella Italia (ID 1)
    (2, '33333333-3333-3333-3333-333333333333'); -- Funcionário 2 no Burger Tech (ID 2)