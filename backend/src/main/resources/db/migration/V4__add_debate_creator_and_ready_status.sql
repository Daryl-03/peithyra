ALTER table debates
    add column creator_id UUID not null
        REFERENCES users (id);

Alter table debates
    drop constraint chk_debate_status;

ALTER table debates
    add constraint chk_debate_status check (
        status in (
                   'WAITING_FOR_OPPONENT',
                   'DONE',
                   'CANCELED',
                   'ONGOING',
                   'WAITING_FOR_READY'
            )
        );

ALTER TABLE debate_participants
    ADD CONSTRAINT fk_debate_participants_user
        FOREIGN KEY (user_id)
            REFERENCES users (id);

ALTER TABLE debate_participants
    ADD column is_ready boolean not null default false;

ALTER TABLE debates
    ADD COLUMN participants_per_side integer not null default 1
        CHECK (participants_per_side >= 1);