-- V5: Thêm cột password_hash vào bảng app_user
-- Dùng BCrypt để lưu mật khẩu, KHÔNG BAO GIỜ lưu plaintext
ALTER TABLE app_user
    ADD COLUMN password_hash VARCHAR(255) NOT NULL DEFAULT '';

-- Seed 1 user mẫu để test đăng nhập
-- password_hash này tương ứng với password: "password123" (BCrypt)
INSERT INTO app_user (username, email, full_name, password_hash)
VALUES ('admin', 'admin@taskflow.com', 'Admin User',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');
