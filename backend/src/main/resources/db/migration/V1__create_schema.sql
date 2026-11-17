CREATE TABLE app_user (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    version      BIGINT       NOT NULL DEFAULT 0,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    modified_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    external_id  VARCHAR(255) NOT NULL,
    email        VARCHAR(320),
    display_name VARCHAR(255),
    CONSTRAINT uq_app_user_external_id UNIQUE (external_id)
);

CREATE TABLE event_category (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    version     BIGINT       NOT NULL DEFAULT 0,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    modified_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    code        VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500)
);

CREATE UNIQUE INDEX uq_event_category_code_active ON event_category (code) WHERE deleted = FALSE;

CREATE TABLE event (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    version     BIGINT        NOT NULL DEFAULT 0,
    deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    modified_at TIMESTAMPTZ   NOT NULL DEFAULT now(),
    category_id BIGINT        NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    summary     TEXT,
    url         VARCHAR(2048),
    occurred_at TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES event_category (id)
);

CREATE INDEX ix_event_category_id ON event (category_id);
CREATE INDEX ix_event_occurred_at ON event (occurred_at DESC);

CREATE TABLE alert_rule (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    version     BIGINT       NOT NULL DEFAULT 0,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    modified_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    owner_id    BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_alert_rule_owner FOREIGN KEY (owner_id) REFERENCES app_user (id)
);

CREATE INDEX ix_alert_rule_owner_id ON alert_rule (owner_id);

CREATE TABLE alert_rule_keyword (
    rule_id BIGINT       NOT NULL,
    keyword VARCHAR(100) NOT NULL,
    CONSTRAINT pk_alert_rule_keyword PRIMARY KEY (rule_id, keyword),
    CONSTRAINT fk_alert_rule_keyword_rule FOREIGN KEY (rule_id) REFERENCES alert_rule (id)
);

CREATE TABLE alert_rule_category (
    rule_id     BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT pk_alert_rule_category PRIMARY KEY (rule_id, category_id),
    CONSTRAINT fk_alert_rule_category_rule FOREIGN KEY (rule_id) REFERENCES alert_rule (id),
    CONSTRAINT fk_alert_rule_category_category FOREIGN KEY (category_id) REFERENCES event_category (id)
);

CREATE INDEX ix_alert_rule_category_category_id ON alert_rule_category (category_id);

CREATE TABLE notification_target (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    version     BIGINT        NOT NULL DEFAULT 0,
    deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    modified_at TIMESTAMPTZ   NOT NULL DEFAULT now(),
    rule_id     BIGINT        NOT NULL,
    channel     VARCHAR(20)   NOT NULL,
    destination VARCHAR(2048) NOT NULL,
    CONSTRAINT fk_notification_target_rule FOREIGN KEY (rule_id) REFERENCES alert_rule (id),
    CONSTRAINT ck_notification_target_channel CHECK (channel IN ('EMAIL', 'SLACK'))
);

CREATE INDEX ix_notification_target_rule_id ON notification_target (rule_id);

CREATE TABLE notification (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    version       BIGINT      NOT NULL DEFAULT 0,
    deleted       BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    modified_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    event_id      BIGINT      NOT NULL,
    target_id     BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL,
    error_message TEXT,
    CONSTRAINT fk_notification_event FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_notification_target FOREIGN KEY (target_id) REFERENCES notification_target (id),
    CONSTRAINT ck_notification_status CHECK (status IN ('SENT', 'FAILED'))
);

CREATE INDEX ix_notification_event_id ON notification (event_id);
CREATE INDEX ix_notification_target_id ON notification (target_id);
