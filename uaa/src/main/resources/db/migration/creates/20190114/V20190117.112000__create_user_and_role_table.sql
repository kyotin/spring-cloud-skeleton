-- Create table user for postgres db (pls change sql structure if use different db )
CREATE TABLE IF NOT EXISTS roles
(
    id serial8 NOT NULL,
    active boolean default true,
    role_name text NOT NULL,
    created timestamptz(0),
    created_by text,
    updated timestamptz(0),
    updated_by text,
    CONSTRAINT roles_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id serial8 NOT NULL,
    active boolean default true,
    user_name text NOT NULL,
    first_name text NOT NULL,
    last_name text,
    role_id bigint,
    factory_id bigint,
    sso text,
    email text,
    password text NOT NULL,
    created timestamptz(0),
    created_by text,
    updated timestamptz(0),
    updated_by text,
    CONSTRAINT users_pkey PRIMARY KEY (user_name),
    CONSTRAINT users_roles_fkey FOREIGN KEY (role_id) REFERENCES roles (id)
);



INSERT INTO roles(role_name) values ('GLOBAL_ADMIN');
INSERT INTO roles(role_name) values ('ADMIN');
INSERT INTO roles(role_name) values ('USER');