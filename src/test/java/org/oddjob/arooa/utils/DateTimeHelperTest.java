package org.oddjob.arooa.utils;


import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DateTimeHelperTest {

    @Test
    void whenVariousIOSInstantsThenParsedOk() {

        Instant expected = Instant.parse("2023-03-02T17:39:00Z");

        assertThat(DateTimeHelper.parseDateTime("2023-03-02T17:39:00Z"), is(expected));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02T17:39:00.000Z"), is(expected));
    }

    @Test
    void whenVariousLegacyThenParsedOk() {

        Instant expected = LocalDateTime.parse("2023-03-02T17:39")
                .atZone(ZoneId.systemDefault())
                .toInstant();

        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:00.000"), is(expected));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:00"), is(expected));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39"), is(expected));
    }
}