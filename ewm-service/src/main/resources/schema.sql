CREATE TABLE IF NOT EXISTS "user"
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS category
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS event
(
    id                 SERIAL PRIMARY KEY,
    annotation         VARCHAR(255) NOT NULL,
    category_id        INTEGER      NOT NULL,
    description        TEXT         NOT NULL,
    event_date         TIMESTAMP    NOT NULL,
    location           VARCHAR(255) NOT NULL,
    paid               BOOLEAN      NOT NULL,
    participant_limit  INTEGER      NOT NULL,
    request_moderation BOOLEAN      NOT NULL,
    title              VARCHAR(255) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE IF NOT EXISTS participation_request
(
    id           SERIAL PRIMARY KEY,
    created      TIMESTAMP    NOT NULL,
    event_id     INTEGER      NOT NULL,
    requester_id INTEGER      NOT NULL,
    status       VARCHAR(255) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event (id),
    FOREIGN KEY (requester_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS compilation
(
    id    SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id INTEGER NOT NULL,
    event_id       INTEGER NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilation (id),
    FOREIGN KEY (event_id) REFERENCES event (id)
);

