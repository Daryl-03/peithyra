create table debates
(
    id          UUID PRIMARY KEY,
    proposition VARCHAR(255) not null,
    description VARCHAR(500),
    status      VARCHAR(50)  not null,
    created_at  TIMESTAMP    not null,
    updated_at  TIMESTAMP    not null,
    started_at  TIMESTAMP,
    ended_at    TIMESTAMP,

    constraint chk_debate_status check (
        status in (
                   'WAITING_FOR_OPPONENT',
                   'DONE',
                   'CANCELED',
                   'IN_PROGRESS'
            )
        )
);

create table debate_participants
(
    debate_id UUID        not null,
    user_id   UUID        not null,
    side      varchar(50) not null,
    joined_at timestamp   not null,

    primary key (debate_id, user_id),

    constraint fk_debate_participants_debate
        foreign key (debate_id)
            references debates (id)
            on delete cascade,

    constraint chk_debate_side check (
        side in (
                   'FOR',
                   'AGAINST'
            )
        )
);