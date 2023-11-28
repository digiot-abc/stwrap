-- stripe_linked_user
CREATE TABLE IF NOT EXISTS stripe_linked_user
(
    id                 VARCHAR(32) PRIMARY KEY,
    user_id            VARCHAR(255)  PRIMARY KEY,
    stripe_customer_id VARCHAR(255) NOT NULL,
    deleted         BOOLEAN   DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_stripe_linked_user_user_id ON stripe_linked_user (user_id);
CREATE INDEX IF NOT EXISTS idx_stripe_linked_user_stripe_customer_id ON stripe_linked_user (stripe_customer_id);

-- stripe_payment_intents
CREATE TABLE IF NOT EXISTS stripe_payment_intents
(
    id                 VARCHAR(32) PRIMARY KEY,
    stripe_linked_user_id VARCHAR(32),
    status VARCHAR(50),
    amount INT,
    currency VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (stripe_linked_user_id) REFERENCES stripe_linked_user(id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_payment_intents_stripe_linked_user_id ON stripe_payment_intents (stripe_linked_user_id);

-- stripe_setup_intents
CREATE TABLE IF NOT EXISTS stripe_setup_intents
(
    id                 VARCHAR(32) PRIMARY KEY,
    stripe_linked_user_id VARCHAR(32),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (stripe_linked_user_id) REFERENCES stripe_linked_user(id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_setup_intents_stripe_linked_user_id ON stripe_setup_intents (stripe_linked_user_id);
