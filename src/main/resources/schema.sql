DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(200),
    available BOOLEAN,
    owner_id BIGINT REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    request_id  BIGINT,
    last_booking_id BIGINT,
    next_booking_id BIGINT
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    start_booking DATE NOT NULL,
    end_booking DATE NOT NULL,
    status VARCHAR(200),
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_booker FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    item_id BIGINT REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    text VARCHAR,
    create_date DATE
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(200),
    requester_id BIGINT REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    created DATE
);