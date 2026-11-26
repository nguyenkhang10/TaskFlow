CREATE TABLE password_reset_token (
                                      id          BIGSERIAL    PRIMARY KEY,
                                      user_id     BIGINT       NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                                      token_hash  VARCHAR(64)  NOT NULL UNIQUE,
                                      expires_at  TIMESTAMPTZ  NOT NULL,
                                      used_at     TIMESTAMPTZ,
                                      created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_prt_user_id ON password_reset_token (user_id);