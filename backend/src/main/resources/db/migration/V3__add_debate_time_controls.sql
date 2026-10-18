ALTER table debates
    add column turn_time_limit_seconds integer not null
        CHECK (turn_time_limit_seconds >= 60),
    add column number_of_turns integer not null
        CHECK (number_of_turns > 0);