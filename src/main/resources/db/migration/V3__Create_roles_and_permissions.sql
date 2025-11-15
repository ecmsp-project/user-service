-- Create permissions table
CREATE TABLE IF NOT EXISTS permissions (
    permission_name VARCHAR(100) PRIMARY KEY
);

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    role_id UUID PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE
);

-- Create role_permissions join table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID REFERENCES roles(role_id) ON DELETE CASCADE,
    permission_name VARCHAR(100) REFERENCES permissions(permission_name) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_name)
);

-- Create user_roles join table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    role_id UUID REFERENCES roles(role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Insert predefined permissions
INSERT INTO permissions (permission_name) VALUES
    ('WRITE_PRODUCTS'),
    ('DELETE_PRODUCTS'),
    ('READ_ORDERS'),
    ('WRITE_ORDERS'),
    ('READ_USERS'),
    ('CANCEL_ORDERS'),
    ('MANAGE_USERS'),
    ('MANAGE_ROLES'),
    ('PROCESS_PAYMENTS'),
    ('REFUND_PAYMENTS');

INSERT INTO roles (role_id, role_name) VALUES
    (RANDOM_UUID(), 'ADMIN'),
    (RANDOM_UUID(), 'MANAGER'),
    (RANDOM_UUID(), 'CUSTOMER_SUPPORT');

INSERT INTO role_permissions (role_id, permission_name) VALUES
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'WRITE_PRODUCTS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'DELETE_PRODUCTS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'READ_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'READ_USERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'WRITE_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'CANCEL_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'MANAGE_USERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'MANAGE_ROLES'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'PROCESS_PAYMENTS'),
    ((SELECT role_id FROM roles WHERE role_name = 'ADMIN'), 'REFUND_PAYMENTS');

INSERT INTO role_permissions (role_id, permission_name) VALUES
    ((SELECT role_id FROM roles WHERE role_name = 'MANAGER'), 'WRITE_PRODUCTS'),
    ((SELECT role_id FROM roles WHERE role_name = 'MANAGER'), 'READ_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'MANAGER'), 'WRITE_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'MANAGER'), 'CANCEL_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'MANAGER'), 'PROCESS_PAYMENTS');

INSERT INTO role_permissions (role_id, permission_name) VALUES
    ((SELECT role_id FROM roles WHERE role_name = 'CUSTOMER_SUPPORT'), 'READ_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'CUSTOMER_SUPPORT'), 'CANCEL_ORDERS'),
    ((SELECT role_id FROM roles WHERE role_name = 'CUSTOMER_SUPPORT'), 'REFUND_PAYMENTS');

INSERT INTO user_roles (user_id, role_id) VALUES
    ((SELECT user_id FROM users WHERE login = 'andy'), (SELECT role_id FROM roles WHERE role_name = 'ADMIN'));

INSERT INTO user_roles (user_id, role_id) VALUES
    ((SELECT user_id FROM users WHERE login = 'john_doe'), (SELECT role_id FROM roles WHERE role_name = 'MANAGER'));
INSERT INTO user_roles (user_id, role_id) VALUES
    ((SELECT user_id FROM users WHERE login = 'jane_smith'), (SELECT role_id FROM roles WHERE role_name = 'CUSTOMER_SUPPORT'));


-- Create indexes for performance
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
