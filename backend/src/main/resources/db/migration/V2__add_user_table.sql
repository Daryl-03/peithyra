create table users
(
    id UUID primary key,
    username varchar(50),
    external_id text not null unique,
    created_at  TIMESTAMPTZ   not null,
    updated_at  TIMESTAMPTZ   not null
);

create unique index users_username_unique
    on users (lower(username));

ALTER TABLE debates
    ALTER COLUMN created_at TYPE timestamptz
        USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at TYPE timestamptz
        USING updated_at AT TIME ZONE 'UTC',
    ALTER COLUMN started_at TYPE timestamptz
        USING started_at AT TIME ZONE 'UTC',
    ALTER COLUMN ended_at TYPE timestamptz
        USING ended_at AT TIME ZONE 'UTC';