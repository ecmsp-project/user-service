-- Insert example users for testing and development

INSERT INTO users (user_id, login, password) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'john_doe', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPKRdQqYBBgOFGhqxfDGqCxo8Ckzq2'), -- password: password123
    ('550e8400-e29b-41d4-a716-446655440001', 'jane_smith', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'), -- password: secret456
    ('550e8400-e29b-41d4-a716-446655440002', 'admin_user', '$2a$10$DOwJoMGp.NiaVisaQeK2eOhZYkyiwQQw2O.CzpG9k4CrcSKTF9q8e'), -- password: admin789
    ('123e4567-e89b-12d3-a456-426614174001', 'andy', '$2a$12$Oox0qAgn0eVRfyNEy1CGIeSRJ9lmMVl7kWaZ5/UPKkWz0c28ZAzDq'); -- password: password123

-- Note: Passwords are hashed using BCrypt with salt rounds = 10
-- These are example hashes for demonstration purposes