
#!/bin/bash
set -e
export PGPASSWORD=$POSTGRES_PASSWORD;
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER $APP_DB_USER WITH PASSWORD '$APP_DB_PASS';
  CREATE DATABASE $APP_DB_NAME;
  GRANT ALL PRIVILEGES ON DATABASE $APP_DB_NAME TO $APP_DB_USER;
  \connect $APP_DB_NAME $APP_DB_USER
  BEGIN;
    CREATE TABLE IF NOT EXISTS public.brokers
    (
        id uuid NOT NULL,
        first_name character varying(250) COLLATE pg_catalog."default" NOT NULL,
        last_name character varying(250) COLLATE pg_catalog."default" NOT NULL,
        CONSTRAINT brokers_pkid PRIMARY KEY (id)
    );
    CREATE TABLE IF NOT EXISTS public.quotes
    (
        id uuid NOT NULL,
        broker_id uuid references brokers(id),
        sex character varying(1) COLLATE pg_catalog."default" NOT NULL,
        expire_at character varying(10) COLLATE pg_catalog."default" NOT NULL,
        price FLOAT,
        CONSTRAINT quotes_pkid PRIMARY KEY (id)
    );
  COMMIT;
EOSQL
