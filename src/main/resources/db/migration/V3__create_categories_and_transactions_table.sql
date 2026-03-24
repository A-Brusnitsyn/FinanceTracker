CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL,
                                          name VARCHAR(100) NOT NULL,
                                          type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                          UNIQUE(user_id, name)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGSERIAL PRIMARY KEY,
                                            user_id BIGINT NOT NULL,
                                            account_id BIGINT NOT NULL,
                                            category_id BIGINT NOT NULL,
                                            amount DECIMAL(15,2) NOT NULL,
                                            description TEXT,
                                            date DATE NOT NULL,
                                            type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                            CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                                            CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Create indexes for categories
CREATE INDEX IF NOT EXISTS idx_categories_user_id ON categories(user_id);

-- Create indexes for transactions
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);