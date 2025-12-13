CREATE TABLE IF NOT EXISTS wallet_users (
    id SERIAL PRIMARY KEY,
    version BIGINT DEFAULT 0,
    user_id VARCHAR(255),
    full_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(50) DEFAULT 'USER',
    active BOOLEAN DEFAULT true,
    merchant_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);