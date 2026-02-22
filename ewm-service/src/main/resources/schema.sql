CREATE TABLE IF NOT EXISTS users
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id                 SERIAL PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    category_id        INTEGER       NOT NULL,
    description        TEXT          NOT NULL,
    event_date         TIMESTAMP     NOT NULL,
    lat                DOUBLE PRECISION NOT NULL,
    lon                DOUBLE PRECISION NOT NULL,
    paid               BOOLEAN       NOT NULL DEFAULT FALSE,
    participant_limit  INTEGER       NOT NULL DEFAULT 0,
    request_moderation BOOLEAN       NOT NULL DEFAULT TRUE,
    title              VARCHAR(120)  NOT NULL,
    initiator_id       INTEGER       NOT NULL,
    state              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    created_on         TIMESTAMP     NOT NULL,
    published_on       TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    id           SERIAL PRIMARY KEY,
    created      TIMESTAMP   NOT NULL,
    event_id     INTEGER     NOT NULL,
    requester_id INTEGER     NOT NULL,
    status       VARCHAR(20) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events (id),
    FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     SERIAL PRIMARY KEY,
    title  VARCHAR(50) NOT NULL,
    pinned BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id INTEGER NOT NULL,
    event_id       INTEGER NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    created_on TIMESTAMP NOT NULL,
    text VARCHAR NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (event_id) REFERENCES events (id)
)
