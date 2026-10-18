package com.peithyra.api.debate.domain;

import com.peithyra.api.debate.internal.domain.DebateFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DebateFormatTest {

    @Test
    void shouldntAllowLowerThanOneRounds() {
        assertThrows(IllegalArgumentException.class, () -> new DebateFormat(0, 60));
    }

    @Test
    void shouldntAllowLowerThanMinimumSecondTimeLimit() {
        assertThrows(IllegalArgumentException.class, () -> new DebateFormat(3, 0));
    }
}
