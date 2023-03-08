package org.oddjob.arooa.utils;


import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

        Instant expected2 = LocalDateTime.parse("2023-03-02T08:39")
                .atZone(ZoneId.systemDefault())
                .toInstant();

        assertThat(DateTimeHelper.parseDateTime("2023-03-02 8:39"), is(expected2));
    }

    @Test
    void whenVariousDecimalsThenParsedOk() {

        Instant expected = LocalDateTime.parse("2023-03-02T17:39:30.9")
                .atZone(ZoneId.systemDefault())
                .toInstant();

        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.9"), is(expected));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.99"),
                is(expected.plusMillis(90L)));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.999"),
                is(expected.plusMillis(99L)));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.9999"),
                is(expected.plusNanos(99900000L)));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.99999"),
                is(expected.plusNanos(99990000L)));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.999999"),
                is(expected.plusNanos(99999000L)));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.9999999"),
                is(expected.plusNanos(99999900L)));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.99999999"),
                is(expected.plusNanos(99999990L)));
        assertThat(DateTimeHelper.parseDateTime("2023-03-02 17:39:30.999999999"),
                is(expected.plusNanos(99999999L)));
    }

    @Test
    void whenDateOnlyThenParsedOk() {

        Instant expected = LocalDate.parse("2023-03-02").atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        assertThat(DateTimeHelper.parseDateTime("2023-03-02"), is(expected));
    }

    @Test
    public void somethingToImplementLater() {

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm")
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .optionalStart()
                .appendLiteral('[')
                .parseCaseSensitive()
                .appendZoneRegionId()
                .appendLiteral(']')
                .toFormatter();

        TemporalAccessor accessor = formatter.parse("2023-03-07 07:10 [Europe/London]");

        ZonedDateTime zonedDateTime = accessor.query(ZonedDateTime::from);

        assertThat(zonedDateTime.toInstant(), is(Instant.parse("2023-03-07T07:10:00Z")));
    }

    @Test
    public void whenClocksGoBackThenLocalTimeIsAmbiguous() throws ParseException {

        // Without time zone this is ambiguous
        String dateTime = "2005-10-30 01:15:00";
        String format = "yyyy-MM-dd HH:mm:ss";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        LocalDateTime ldt = formatter.parse(dateTime).query(LocalDateTime::from);

        ZoneId zoneId = ZoneId.of("Europe/London");

        ZonedDateTime zdt = ldt.atZone(zoneId);

        // New Java time decides it's still BST.
        MatcherAssert.assertThat(zdt.getOffset().getId(), is("+01:00"));

        Instant instant = zdt.toInstant();

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        dateFormat.setTimeZone(timeZone);

        Date date = dateFormat.parse(dateTime);

        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(date);

        // Old Java Time thinks it's GMT
        MatcherAssert.assertThat(cal.get(Calendar.DST_OFFSET), is(0));

        MatcherAssert.assertThat(Date.from(instant.plus(1, ChronoUnit.HOURS)), is(date));
    }


}