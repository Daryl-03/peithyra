package com.peithyra.api.debate.internal.domain;

public record DebateFormat(
        int numberOfTurns,
        int turnTimeLimitSeconds,
        int participantsPerSide
) {
    public DebateFormat {
        if (numberOfTurns < 1) {
            throw new IllegalArgumentException("Number of turns must be at least 1");
        }
        if (turnTimeLimitSeconds < 60) {
            throw new IllegalArgumentException("Turn time limit must be at least 60 seconds");
        }
        if (participantsPerSide < 1) {
            throw new IllegalArgumentException("Participants per side must be at least 1");
        }
    }

    public DebateFormat(int numberOfTurns, int turnTimeLimitSeconds) {
        this(numberOfTurns, turnTimeLimitSeconds, 1);
    }
}
