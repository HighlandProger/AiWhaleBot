CREATE ROLE whale_ai_bot;
ALTER USER whale_ai_bot WITH PASSWORD '3456';
ALTER ROLE whale_ai_bot WITH LOGIN;

CREATE DATABASE whale_ai_db WITH OWNER whale_ai_bot;
====================================================
psql whale_ai_db;
====================================================

CREATE SCHEMA IF NOT EXISTS ncs_bot;
SET
    SEARCH_PATH = ncs_bot;

CREATE TABLE ncs_bot.chats
(
    id                bigserial primary key,
    username          varchar,
    telegram_first_name varchar,
    telegram_last_name varchar,
    next_command      varchar,
    registration_time timestamp(0),
    is_admin          boolean default false,
    start_info        varchar,
    traffic_link      varchar,
    campaign      varchar,
    assistant_type varchar,
    subscription_id int8,
    ai_language     varchar not null default 'ru',
    temperature     float4 not null default '1.0',
    ai_user_model   varchar not null
);

CREATE TABLE ncs_bot.log_events
(
    id        bigserial,
    chat_id   int8,
    event_time timestamp,
    event      varchar
);

CREATE TABLE ncs_bot.telegram_data
(
    id                     bigserial primary key,
    name                   varchar,
    text_message           text,
    photo_id               text,
    video_id               text,
    sticker_id             text
);

CREATE TABLE ncs_bot.orders
(
    id                     bigserial primary key,
    info                   varchar,
    price                  decimal,
    created_at             timestamp,
    purchased_at           timestamp,
    invoice_url            varchar,
    is_purchased           boolean
);


CREATE TABLE ncs_bot.tasks
(
    id                     bigserial primary key,
    chat_id                int8 not null,
    command_to_execute     varchar not null,
    execute_time           timestamp not null,
    command_to_check       varchar
);

CREATE TABLE ncs_bot.subscriptions (
    id bigserial,
    subscription_info_id int8,
    purchase_time timestamp,
    expiration_date timestamp,
    purchase_type VARCHAR(255),
    chat_id int8,
    FOREIGN KEY (chat_id) REFERENCES ncs_bot.chats(id)
);

CREATE TABLE ncs_bot.subscription_info (
    id bigserial PRIMARY KEY,
    type VARCHAR,
    smile VARCHAR,
    ru_name VARCHAR,
    month_cost FLOAT,
    year_cost FLOAT,
    gpt_3_day_limit INT,
    gpt_4_day_limit INT,
    image_day_limit INT,
    song_month_limit INT,
    claude_tokens_month_limit INT,
    claude_sale_size INT
);

GRANT ALL PRIVILEGES ON DATABASE whale_ai_db TO whale_ai_bot;
GRANT SELECT ON ALL TABLES IN SCHEMA ncs_bot TO whale_ai_bot;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ncs_bot TO whale_ai_bot;
GRANT USAGE, SELECT ON SEQUENCE log_events_id_seq TO whale_ai_bot;
GRANT USAGE, SELECT ON SEQUENCE tasks_id_seq TO whale_ai_bot;
GRANT USAGE, SELECT ON SEQUENCE subscriptions_id_seq TO whale_ai_bot;