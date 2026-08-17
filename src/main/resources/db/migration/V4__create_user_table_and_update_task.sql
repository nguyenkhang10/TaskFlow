CREATE TABLE app_user (
                          id          BIGSERIAL    PRIMARY KEY,
                          username    VARCHAR(60)  NOT NULL UNIQUE,
                          email       VARCHAR(120) NOT NULL UNIQUE,
                          full_name   VARCHAR(120) NOT NULL,
                          status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                          created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

                          CONSTRAINT check_user_status CHECK (
                              status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')
                              )
);

CREATE INDEX idx_user_username ON app_user (username);
CREATE INDEX idx_user_email ON app_user (email);

ALTER TABLE task
    ADD COLUMN creator_id BIGINT REFERENCES app_user(id) ON DELETE SET NULL,
    ADD COLUMN handler_id BIGINT REFERENCES app_user(id) ON DELETE SET NULL;

CREATE INDEX idx_task_creator_id ON task (creator_id);
CREATE INDEX idx_task_handler_id ON task (handler_id);