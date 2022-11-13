BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS CURRENCY (
                          id SERIAL PRIMARY KEY,
                          code varchar(10) UNIQUE NOT NULL,
                          name varchar(20) NOT NULL,
                          type varchar(6) NOT NULL
);

CREATE INDEX IF NOT EXISTS currency_id ON CURRENCY USING hash(id);
CREATE INDEX IF NOT EXISTS currency_code ON CURRENCY USING hash(code);

CREATE TABLE IF NOT EXISTS CURRENCY_EXCHANGE (
                                   id SERIAL PRIMARY KEY,
                                   from_id int NOT NULL,
                                   to_id int NOT NULL,
                                   foreign key (from_id) references CURRENCY(id),
                                   foreign key (to_id) references CURRENCY(id)
);

CREATE INDEX IF NOT EXISTS currency_exchange_id ON CURRENCY_EXCHANGE USING hash(id);

CREATE TABLE IF NOT EXISTS CONVERT_REQUEST (
                                 id SERIAL PRIMARY KEY,
                                 exchange_id int NOT NULL,
                                 quantity decimal NOT NULL,
                                 rate decimal NOT NULL,
                                 user_id int NOT NULL,
                                 created_at timestamptz NOT NULL DEFAULT now(),
                                 foreign key (exchange_id) references CURRENCY_EXCHANGE(id)
);

CREATE INDEX IF NOT EXISTS convert_request_id ON CONVERT_REQUEST USING hash(id);

COMMIT;