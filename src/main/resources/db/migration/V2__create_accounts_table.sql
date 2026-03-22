-- V2__create_accounts_table.sql
CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        name VARCHAR(100) NOT NULL,
                                        balance DECIMAL(15,2) DEFAULT 0.00,
                                        currency VARCHAR(3) DEFAULT 'RUB',
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);