-- V6: Add role and deleted_at columns to app_user table
ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

UPDATE app_user
SET role = 'ADMIN'
WHERE username = 'admin';
