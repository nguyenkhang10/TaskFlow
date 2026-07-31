-- V2: Dữ liệu mẫu để Kids có sẵn cái mà GET/UPDATE/DELETE.

INSERT INTO project (name, description, status, owner, start_date, due_date) VALUES
        ('Khoa Hoc SAT',           'khoa hoc SAT cua Khoa',                     'ACTIVE',        'Khoa',   '2026-03-01', '2026-09-30'),
        ('Khoa Hoc IELTS',         'Khoa hoc IELTS cua Khoa',                   'ACTIVE',        'Khoa',   '2026-01-15', '2026-12-31'),
        ('TaskFlow Capstone',      'Dự án tốt nghiệp của CTEL Kids',            'PLANNING',      'kids',   '2026-07-01', '2026-09-20'),
        ('Khoa hoc AP',            'Khoa hoc AP cua Khoa',                      'ON_HOLD',       'Khoa',   '2026-06-07', '2027-07-07'),
        ('Khoa hoc A-level',       'Khoa hoc A-level cua Khoa',                 'COMPLETED',     'Khoa',   '2025-06-01', '2028-12-15');