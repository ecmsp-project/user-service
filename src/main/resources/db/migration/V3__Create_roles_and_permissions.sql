-- Create permissions table
CREATE TABLE permissions (
    permission_name VARCHAR(100) PRIMARY KEY
);

-- Create roles table
CREATE TABLE roles (
    role_id UUID PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE
);

-- Create role_permissions join table
CREATE TABLE role_permissions (
    role_id UUID REFERENCES roles(role_id) ON DELETE CASCADE,
    permission_name VARCHAR(100) REFERENCES permissions(permission_name) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_name)
);

-- Create user_roles join table
CREATE TABLE user_roles (
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
    ('CANCEL_ORDERS'),
    ('MANAGE_USERS'),
    ('MANAGE_ROLES'),
    ('PROCESS_PAYMENTS'),
    ('REFUND_PAYMENTS');

-- Create indexes for performance
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
