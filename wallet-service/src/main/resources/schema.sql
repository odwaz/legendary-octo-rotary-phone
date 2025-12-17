CREATE TABLE IF NOT EXISTS wallets (
    wallet_id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT,
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'ZAR',
    wallet_alias VARCHAR(255) UNIQUE,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);