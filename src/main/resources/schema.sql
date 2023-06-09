CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(30),
    name VARCHAR(30),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id BIGINT,
    name VARCHAR(30),
    description VARCHAR(255),
    is_available BOOLEAN,
    CONSTRAINT fk_owner_id_to_user
    FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_time DATETIME,
    end_time DATETIME,
    status VARCHAR(30),
    user_id BIGINT,
    item_id BIGINT,
    CONSTRAINT fk_user_id_to_user
    FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_item_id_to_item
    FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(255),
    item_id BIGINT,
    author_id BIGINT,
    created DATETIME,
    CONSTRAINT fk_comments_item_id_to_item
    FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comments_user_id_to_user
    FOREIGN KEY (author_id) REFERENCES users (id)
);