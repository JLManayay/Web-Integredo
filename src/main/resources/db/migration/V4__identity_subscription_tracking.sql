-- Cuentas con plan y entrenador, sesiones, pagos y registro de adherencia.
ALTER TABLE users ADD COLUMN plan VARCHAR(10) NOT NULL DEFAULT 'GRATIS';
ALTER TABLE users ADD COLUMN pro_since TIMESTAMP NULL;
ALTER TABLE users ADD COLUMN coach_id UUID NULL REFERENCES users(id);
ALTER TABLE users ADD COLUMN brand_name VARCHAR(40) NULL;
ALTER TABLE users ADD COLUMN brand_color VARCHAR(7) NULL;

CREATE TABLE user_sessions (
    token_hash  VARCHAR(64) PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at  TIMESTAMP NOT NULL
);

CREATE TABLE payments (
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users(id),
    amount     DECIMAL(8,2) NOT NULL,
    currency   VARCHAR(3) NOT NULL,
    method     VARCHAR(20) NOT NULL,
    reference  VARCHAR(40) NOT NULL,
    paid_at    TIMESTAMP NOT NULL
);

CREATE TABLE adherence_checkins (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id),
    check_date  DATE NOT NULL,
    kind        VARCHAR(10) NOT NULL,
    ref_id      UUID NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_checkin UNIQUE (user_id, check_date, kind, ref_id)
);

CREATE INDEX idx_users_coach_id ON users(coach_id);
CREATE INDEX idx_checkins_user_date ON adherence_checkins(user_id, check_date);
