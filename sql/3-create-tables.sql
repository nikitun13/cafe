CREATE TYPE user_role AS ENUM ('ADMIN', 'CLIENT');

CREATE TYPE order_status AS ENUM ('CANCELED', 'COMPLETED', 'NOT_COLLECTED', 'PENDING');

CREATE TYPE dish_category AS ENUM ('PIZZA', 'SNACKS', 'DESSERTS', 'SAUCES', 'DRINKS', 'SALADS');

CREATE TABLE users
(
    id         BIGSERIAL,
    email      VARCHAR(254) NOT NULL UNIQUE,
    password   CHAR(60)     NOT NULL,
    role       user_role    NOT NULL,
    first_name VARCHAR(128) NOT NULL,
    last_name  VARCHAR(128) NOT NULL,
    phone      VARCHAR(16)  NOT NULL UNIQUE,
    points     BIGINT       NOT NULL,
    is_blocked BOOLEAN      NOT NULL,
    CONSTRAINT PK_users PRIMARY KEY (id)
);

CREATE TABLE dish
(
    id          BIGSERIAL,
    name        VARCHAR(128)  NOT NULL,
    category    dish_category NOT NULL,
    price       BIGINT        NOT NULL,
    description VARCHAR(1024) NOT NULL,
    CONSTRAINT PK_dish PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id                     BIGSERIAL,
    user_id                BIGINT       NOT NULL,
    created_at             TIMESTAMP    NOT NULL,
    expected_retrieve_date TIMESTAMP    NOT NULL,
    actual_retrieve_date   TIMESTAMP,
    status                 order_status NOT NULL,
    debited_points         BIGINT       NOT NULL,
    accrued_points         BIGINT       NOT NULL,
    total_price            BIGINT       NOT NULL,
    CONSTRAINT PK_orders PRIMARY KEY (id),
    CONSTRAINT FK_orders_users FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE dish_orders
(
    order_id   BIGINT,
    dish_id    BIGINT,
    dish_price BIGINT,
    dish_count SMALLINT NOT NULL,
    CONSTRAINT PK_dish_orders PRIMARY KEY (order_id, dish_id),
    CONSTRAINT FK_dish_orders_orders FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT FK_dish_orders_dish FOREIGN KEY (dish_id) REFERENCES dish (id)
);

CREATE TABLE comment
(
    id         BIGSERIAL,
    user_id    BIGINT        NOT NULL,
    dish_id    BIGINT        NOT NULL,
    rating     SMALLINT      NOT NULL CHECK ( rating IN (1, 2, 3, 4, 5) ),
    body       VARCHAR(1024) NOT NULL,
    created_at TIMESTAMP     NOT NULL,
    CONSTRAINT PK_comment PRIMARY KEY (id),
    CONSTRAINT FK_comment_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT FK_comment_dish FOREIGN KEY (dish_id) REFERENCES dish (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX users_email_idx
    ON users (email);

CREATE INDEX dish_category_idx
    ON dish (category);

CREATE INDEX orders_user_id_idx
    ON orders (user_id);

CREATE INDEX orders_status_idx
    ON orders (status);

CREATE INDEX comment_dish_id_idx
    ON comment (dish_id);

GRANT SELECT, INSERT, UPDATE, DELETE
    ON ALL TABLES IN SCHEMA public
    TO cafe_user;

GRANT USAGE, SELECT
    ON ALL SEQUENCES IN SCHEMA public
    TO cafe_user;
