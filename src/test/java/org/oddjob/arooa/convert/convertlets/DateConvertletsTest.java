/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.arooa.utils.DateTimeHelper;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DateConvertletsTest {

    private static Date parse(String s) {
        try {
            return DateHelper.parseDateTime(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testAll() throws NoConversionAvailableException, ConversionFailedException {
        ArooaConverter converter = DefaultConverter.from(new DateConvertlets());

        // Date -> String
        assertThat(converter.convert(parse("2007-12-25 12:57"), String.class),
                is("2007-12-25 12:57:00.000"));
        assertThat(converter.convert(parse("2007-12-25"), String.class),
                is("2007-12-25 00:00:00.000"));

        // String -> Date
        assertThat(converter.convert("2007-12-25 12:57", Date.class),
                is(parse("2007-12-25 12:57")));
        assertThat(converter.convert("2007-12-25 00:00", Date.class),
                is(parse("2007-12-25")));

        // Long -> Date
        assertThat(converter.convert(100L, Date.class),
                is(new Date(100)));

        // Date -> Long
        assertThat(converter.convert(new Date(100), long.class),
                is(100L));

        // Date -> Instant
        Instant instant = DateTimeHelper.parseDateTime("2023-03-03T18:14:00Z");
        assertThat(converter.convert(Date.from(instant), Instant.class),
                is(instant));

        // Date -> Instant
        assertThat(converter.convert(instant, Date.class),
                is(Date.from(instant)));
    }

}
