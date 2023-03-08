package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaConstants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Date and Time Utilities that use Instant. This will gradually phase out {@link DateHelper}.
 *
 * @author Rob Gordon.
 *
 */
public class DateTimeHelper {

    /**
     * Parse format for {@link org.oddjob.arooa.ArooaConstants} date time formats.
     */
    public static final DateTimeFormatter LEGACY_DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern(ArooaConstants.DATE_FORMAT + " H:mm[:ss]")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    /**
     *
     * @param text A date and time as text.
     * @return The instant equivalent.
     */
    public static Instant parseDateTime(String text) {

        return parseDateTime(text, ZoneId.systemDefault());
    }

    /**
     *
     * @param text A date and time as text.
     * @param zoneId The time zone.
     *
     * @return The instant equivalent.
     */
    public static Instant parseDateTime(String text, ZoneId zoneId) {

        // We can do better than this... later.
        if (text.contains("T")) {
            return Instant.parse(text);
        }
        else if (text.contains(" ")) {
            return LEGACY_DATE_TIME_FORMAT.withZone(zoneId).parse(text, Instant::from);
        }
        else {
            return LocalDate.parse(text).atStartOfDay().atZone(zoneId).toInstant();
        }
    }
}
