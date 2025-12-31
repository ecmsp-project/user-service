
CREATE TABLE users
(
    user_id UUID PRIMARY KEY,
    login VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);


CREATE INDEX idx_users_login ON users (login);