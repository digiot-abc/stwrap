-- stripe_linked_user
CREATE TABLE IF NOT EXISTS stripe_linked_user
(
    id                 VARCHAR(32) PRIMARY KEY,
    user_id            VARCHAR(255) NOT NULL,
    stripe_customer_id VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT TRUE,
    deleted         BOOLEAN   DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_stripe_linked_user_user_id ON stripe_linked_user (user_id);
CREATE INDEX IF NOT EXISTS idx_stripe_linked_user_stripe_customer_id ON stripe_linked_user (stripe_customer_id);

-- stripe_subscription
CREATE TABLE IF NOT EXISTS stripe_subscription
(
    id                  VARCHAR(32) PRIMARY KEY,
    stripe_linked_user_id VARCHAR(32)  NOT NULL,
    subscription_id     VARCHAR(255) NOT NULL UNIQUE,
    plan_id             VARCHAR(255) NOT NULL,
    status              VARCHAR(255) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (stripe_linked_user_id) REFERENCES stripe_linked_user (id)
);

CREATE INDEX IF NOT EXISTS idx_stripe_subscription_stripe_linked_user_id ON stripe_subscription (stripe_linked_user_id);
CREATE INDEX IF NOT EXISTS idx_stripe_subscription_stripe_subscription_id ON stripe_subscription (subscription_id);

-- stripe_payment
CREATE TABLE IF NOT EXISTS stripe_payment
(
    id                  VARCHAR(32) PRIMARY KEY,
    stripe_linked_user_id VARCHAR(32)    NOT NULL,
    stripe_charge_id    VARCHAR(255)   NOT NULL,
    amount              DECIMAL(10, 2) NOT NULL,
    currency            VARCHAR(10)    NOT NULL,
    status              VARCHAR(255)   NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (stripe_linked_user_id) REFERENCES stripe_linked_user (id)
);

CREATE INDEX IF NOT EXISTS idx_stripe_payment_stripe_linked_user_id ON stripe_payment (stripe_linked_user_id);
CREATE INDEX IF NOT EXISTS idx_stripe_payment_stripe_charge_id ON stripe_payment (stripe_charge_id);
