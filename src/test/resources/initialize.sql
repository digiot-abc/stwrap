-- stripe_linked_user
CREATE TABLE IF NOT EXISTS stripe_linked_user
(
    id                 VARCHAR(32) PRIMARY KEY,
--     user_id            VARCHAR(255) NOT NULL,
    user_id            INT NOT NULL,
    stripe_customer_id VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT TRUE,
    deleted         BOOLEAN   DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_stripe_linked_user_user_id ON stripe_linked_user (user_id);
CREATE INDEX IF NOT EXISTS idx_stripe_linked_user_stripe_customer_id ON stripe_linked_user (stripe_customer_id);
