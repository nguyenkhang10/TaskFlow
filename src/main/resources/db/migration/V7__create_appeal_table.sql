CREATE TABLE appeal (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(60)  NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
