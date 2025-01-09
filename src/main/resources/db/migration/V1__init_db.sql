-- Create sequence for Users table
CREATE SEQUENCE IF NOT EXISTS seq_users_id
    START WITH 1
    INCREMENT BY 1;

-- Create sequence for URLs table
CREATE SEQUENCE IF NOT EXISTS seq_urls_id
    START WITH 1
    INCREMENT BY 1;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT DEFAULT nextval('seq_users_id'),
    username VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL CHECK (length(password) >= 8 AND password ~ '^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$'),
    role VARCHAR(10) NOT NULL CHECK (role IN ('Admin', 'User', 'Owner')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_users_id PRIMARY KEY (id)
);

-- Create URLs table
CREATE TABLE IF NOT EXISTS urls (
    id BIGINT DEFAULT nextval('seq_urls_id'),
    short_url VARCHAR(10) UNIQUE NOT NULL,
    original_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    click_count BIGINT DEFAULT 0,
    user_id BIGINT,
    CONSTRAINT pk_urls_id PRIMARY KEY (id),
    CONSTRAINT fk_urls_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create indexes
CREATE INDEX idx_urls_user_id ON urls (user_id);