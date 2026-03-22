-- create_tables.sql
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     email VARCHAR(255) UNIQUE NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     name VARCHAR(100),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL REFERENCES users(id),
                                          name VARCHAR(100) NOT NULL,
                                          type VARCHAR(10) CHECK (type IN ('INCOME', 'EXPENSE')),
                                          color VARCHAR(7) DEFAULT '#808080',
                                          UNIQUE(user_id, name)
);

CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL REFERENCES users(id),
                                        name VARCHAR(100) NOT NULL,
                                        balance DECIMAL(15,2) DEFAULT 0.00,
                                        currency VARCHAR(3) DEFAULT 'RUB',
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGSERIAL PRIMARY KEY,
                                            account_id BIGINT NOT NULL REFERENCES accounts(id),
                                            category_id BIGINT NOT NULL REFERENCES categories(id),
                                            amount DECIMAL(15,2) NOT NULL,
                                            description TEXT,
                                            date DATE NOT NULL,
                                            type VARCHAR(10) CHECK (type IN ('INCOME', 'EXPENSE')),
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- index for fast search
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_date ON transactions(date);
CREATE INDEX idx_transactions_type ON transactions(type);