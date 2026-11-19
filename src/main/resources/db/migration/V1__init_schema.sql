-- Migración inicial. Traduce al esquema relacional los agregados
-- descritos en el Capítulo 3 del informe (Figura 3.7 y diccionario
-- de datos 3.4.8).

CREATE TABLE users (
    id              UUID PRIMARY KEY,
    full_name       VARCHAR(120) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE workout_programs (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users(id),
    name            VARCHAR(120) NOT NULL,
    split_type      VARCHAR(30)  NOT NULL,
    published       BOOLEAN      NOT NULL DEFAULT false,
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE workout_days (
    id              UUID PRIMARY KEY,
    program_id      UUID NOT NULL REFERENCES workout_programs(id) ON DELETE CASCADE,
    day_of_week     VARCHAR(10) NOT NULL
);

CREATE TABLE exercises (
    id              UUID PRIMARY KEY,
    day_id          UUID NOT NULL REFERENCES workout_days(id) ON DELETE CASCADE,
    name            VARCHAR(120) NOT NULL,
    muscle_group    VARCHAR(60)  NOT NULL,
    sets            INTEGER  NOT NULL,
    reps_per_set    INTEGER  NOT NULL,
    rest_seconds    BIGINT   NOT NULL
);

CREATE TABLE supplement_plans (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users(id),
    program_id      UUID NULL REFERENCES workout_programs(id),
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE supplement_doses (
    id                  UUID PRIMARY KEY,
    plan_id             UUID NOT NULL REFERENCES supplement_plans(id) ON DELETE CASCADE,
    supplement_name     VARCHAR(80) NOT NULL,
    amount_mg           DECIMAL(8,2) NOT NULL,
    time_of_day         TIME NOT NULL,
    relative_to_workout BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_workout_programs_user_id ON workout_programs(user_id);
CREATE INDEX idx_supplement_plans_user_id ON supplement_plans(user_id);
CREATE INDEX idx_supplement_plans_program_id ON supplement_plans(program_id);
