-- stripe_user_link
CREATE TABLE stripe_user_link
(
    id                 VARCHAR(32) PRIMARY KEY,
    user_id            VARCHAR(255) NOT NULL,
    stripe_customer_id VARCHAR(255) NOT NULL,
    is_deleted         BOOLEAN   DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_stripe_user_link_user_id ON stripe_user_link (user_id);
CREATE INDEX idx_stripe_user_link_stripe_customer_id ON stripe_user_link (stripe_customer_id);

-- stripe_subscription
CREATE TABLE stripe_subscription
(
    id                  VARCHAR(32) PRIMARY KEY,
    stripe_user_link_id VARCHAR(32)  NOT NULL,
    subscription_id     VARCHAR(255) NOT NULL UNIQUE,
    plan_id             VARCHAR(255) NOT NULL,
    status              VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (stripe_user_link_id) REFERENCES stripe_user_link (id)
);

CREATE INDEX idx_stripe_subscription_stripe_user_link_id ON stripe_subscription (stripe_user_link_id);
CREATE INDEX idx_stripe_subscription_stripe_subscription_id ON stripe_subscription (subscription_id);

-- stripe_payment
CREATE TABLE stripe_payment
(
    id                  VARCHAR(32) PRIMARY KEY,
    stripe_user_link_id VARCHAR(32)    NOT NULL,
    stripe_charge_id    VARCHAR(255)   NOT NULL,
    amount              DECIMAL(10, 2) NOT NULL,
    currency            VARCHAR(10)    NOT NULL,
    status              VARCHAR(255)   NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (stripe_user_link_id) REFERENCES stripe_user_link (id)
);

CREATE INDEX idx_stripe_payment_stripe_user_link_id ON stripe_payment (stripe_user_link_id);
CREATE INDEX idx_stripe_payment_stripe_charge_id ON stripe_payment (stripe_charge_id);
