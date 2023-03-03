/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.utils.DateTimeHelper;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DateTimeConvertletsTest {


    @Test
    void testAll() throws NoConversionAvailableException, ConversionFailedException {

        ArooaConverter converter = DefaultConverter.from(new DateTimeConvertlets());

        String string = "2023-03-03T18:14:00Z";
        Instant instant = DateTimeHelper.parseDateTime(string);
        long millis = instant.toEpochMilli();

        // Instant -> String
        assertThat(converter.convert(instant, String.class), is(string));

        // String -> Instant
        assertThat(converter.convert(string, Instant.class), is(instant));

        // Long -> Instant
        assertThat(converter.convert(millis, Instant.class), is(instant));

        // Instant -> Long
        assertThat(converter.convert(instant, long.class),
                is(millis));

    }

}
