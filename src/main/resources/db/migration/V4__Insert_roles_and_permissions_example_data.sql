-- Insert example roles

INSERT INTO roles (role_id, role_name) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ADMIN'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'USER'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'MANAGER'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'CUSTOMER_SUPPORT');

-- Assign permissions to ADMIN role (full access)
INSERT INTO role_permissions (role_id, permission_name) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'READ_PRODUCTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'WRITE_PRODUCTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'DELETE_PRODUCTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'READ_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'WRITE_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CANCEL_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'READ_CARTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'WRITE_CARTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'MANAGE_USERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'MANAGE_ROLES'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'PROCESS_PAYMENTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'REFUND_PAYMENTS');

-- Assign permissions to USER role (basic access)
INSERT INTO role_permissions (role_id, permission_name) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'READ_PRODUCTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'READ_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'WRITE_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'READ_CARTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'WRITE_CARTS');

-- Assign permissions to MANAGER role (moderate access)
INSERT INTO role_permissions (role_id, permission_name) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'READ_PRODUCTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'WRITE_PRODUCTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'READ_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'WRITE_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'CANCEL_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'READ_CARTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'PROCESS_PAYMENTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'REFUND_PAYMENTS');

-- Assign permissions to CUSTOMER_SUPPORT role (support access)
INSERT INTO role_permissions (role_id, permission_name) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'READ_PRODUCTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'READ_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'CANCEL_ORDERS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'READ_CARTS'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'REFUND_PAYMENTS');

-- Assign roles to existing users
-- admin_user gets ADMIN role
INSERT INTO user_roles (user_id, role_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440002', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- john_doe gets USER role
INSERT INTO user_roles (user_id, role_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12');

-- jane_smith gets MANAGER role
INSERT INTO user_roles (user_id, role_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13');

-- andy gets USER and CUSTOMER_SUPPORT roles (example of multiple roles)
INSERT INTO user_roles (user_id, role_id) VALUES
    ('123e4567-e89b-12d3-a456-426614174001', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12'),
    ('123e4567-e89b-12d3-a456-426614174001', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14');