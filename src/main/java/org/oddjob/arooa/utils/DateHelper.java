/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Date Helper Utilities
 *
 * @author Rob Gordon.
 */
public class DateHelper {

    /**
     * Parse a date and time. The input can either be just a date
     * or a date and a time.
     *
     * @param text The date time.
     * @return The date for the given text in the current time zone.
     * @throws DateTimeParseException If the text isn't in a recognised
     *                                date/time format.
     */
    public static Date parseDateTime(String text) throws DateTimeParseException {
        return parseDateTime(text, TimeZone.getDefault());
    }

    /**
     * Parse a date and time in the given time zone. The input can
     * either be just a date or a date and time.
     *
     * @param text       The date time.
     * @param timeZoneId The time zone identifier.
     * @return The date for the given text in the specified time zone.
     * @throws ParseException If the text isn't in a recognised
     *                        date/time format.
     */
    public static Date parseDateTime(String text, String timeZoneId)
            throws ParseException {
        TimeZone timeZone = TimeZone.getDefault();
        if (timeZoneId != null) {
            timeZone = TimeZone.getTimeZone(timeZoneId);
        }
        return parseDateTime(text, timeZone);
    }

    /**
     * Parse a date and time in the given time zone. The input can
     * either be just a date or a date and time.
     *
     * @param text     The date time
     * @param timeZone The timeZone.
     * @return The date for the given text in the specified time zone.
     * @throws DateTimeParseException If the text isn't in a recognised
     *                                date/time format.
     */
    public static Date parseDateTime(String text, TimeZone timeZone)
            throws DateTimeParseException {
        return Date.from(DateTimeHelper.parseDateTime(text,
                Objects.requireNonNullElse(timeZone, TimeZone.getDefault()).toZoneId()));
    }

    /**
     * Parse a date using the default time zone.
     *
     * @param text A date.
     * @return The date for the given text.
     * @throws DateTimeParseException If the date isn't in the recognised
     *                        date format.
     */
    public static Date parseDate(String text) {
        return parseDate(text, TimeZone.getDefault());
    }

    /**
     * Parse a date using the given time zone.
     *
     * @param text       The date text.
     * @param timeZoneId The time zone identifier.
     * @return The date for the given text in the specified time zone.
     * @throws DateTimeParseException If the date isn't in the recognised
     *                                date format.
     */
    public static Date parseDate(String text, String timeZoneId) throws DateTimeParseException {
        return parseDate(text,
                timeZoneId == null ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZoneId));
    }

    /**
     * Parse a date using the given time zone.
     *
     * @param text     The date text.
     * @param timeZone The time zone.
     * @return The date for the given text in the specified time zone.
     * @throws DateTimeParseException If the date isn't in the recognised
     *                                date format.
     */
    public static Date parseDate(String text, TimeZone timeZone) throws DateTimeParseException {
        try {
            return parse(text, ArooaConstants.DATE_FORMAT, timeZone);
        } catch (ParseException e) {
            throw new DateTimeParseException(e.getMessage() +
                    ", valid format is " + ArooaConstants.DATE_FORMAT,
                    text,
                    e.getErrorOffset());
        }

    }

    /**
     * Parse a time into a number of milliseconds.
     *
     * @param text The time.
     * @return The time as milliseconds.
     * @throws ParseException If parsing fails.
     */
    public static long parseTime(String text) throws ParseException {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+00");
        Date d;
        try {
            d = parse(text, ArooaConstants.TIME_FORMAT1, timeZone);
        } catch (ParseException e) {
            try {
                d = parse(text, ArooaConstants.TIME_FORMAT2, timeZone);
            } catch (ParseException e2) {
                try {
                    d = parse(text, ArooaConstants.TIME_FORMAT3, timeZone);
                } catch (ParseException e3) {
                    throw new ParseException(e.getMessage() +
                            ", valid formats are " +
                            ArooaConstants.TIME_FORMAT1 + ", " +
                            ArooaConstants.TIME_FORMAT2 + " or " +
                            ArooaConstants.TIME_FORMAT3, e.getErrorOffset());
                }
            }
        }
        return d.getTime();
    }

    /**
     * Format a date into just text representing just the date.
     *
     * @param date The date
     * @return The text equivalent.
     */
    public static String formatDate(Date date) {
        return formatDate(date, null);
    }

    /**
     * Format a date into just text representing just the date.
     *
     * @param date     The date
     * @param timeZone The time zone.
     * @return The text equivalent.
     */
    public static String formatDate(Date date, TimeZone timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(ArooaConstants.DATE_FORMAT);
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format.format(date);
    }

    /**
     * Format a date into full date/time text.
     *
     * @param date The date
     * @return The text equivalent.
     */
    public static String formatDateTime(Date date) {
        return formatDateTime(date, null);
    }

    /**
     * Format a date into full date/time text.
     *
     * @param date     The date
     * @param timeZone The time zone.
     * @return The text equivalent.
     */
    public static String formatDateTime(Date date, TimeZone timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(ArooaConstants.DATE_FORMAT + " " +
                ArooaConstants.TIME_FORMAT1);
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format.format(date);
    }

    /**
     * Format date time with or without milliseconds.
     *
     * @param date The date.
     * @return A formatted date.
     */
    public static String formatDateTimeIntelligently(Date date) {
        return formatDateTimeIntelligently(date, null);
    }

    /**
     * Format date time with or without milliseconds depending on if they are present.
     *
     * @param date     The date.
     * @param timeZone The timezone.
     * @return A formatted date.
     */
    public static String formatDateTimeIntelligently(Date date, TimeZone timeZone) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat format;
        if (date.getTime() % 1000 == 0) {
            // no milliseconds - then miss them off.
            format = new SimpleDateFormat(
                    ArooaConstants.DATE_FORMAT + " " +
                            ArooaConstants.TIME_FORMAT2);
        } else {
            format = new SimpleDateFormat(
                    ArooaConstants.DATE_FORMAT + " " +
                            ArooaConstants.TIME_FORMAT1);
        }

        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }

        return format.format(date);
    }

    public static String formatMilliseconds(long milliseconds) {

        long seconds = milliseconds / 1000;

        if (seconds < 60) {
            return "" + seconds + " second" + s(seconds);
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes < 60) {
            return "" + minutes + " minute" + s(minutes) + " "
                    + seconds + " second" + s(seconds);
        }

        long hours = minutes / 60;
        minutes = minutes % 60;

        if (hours < 24) {
            return "" + hours + " hour" + s(hours) + " " +
                    minutes + " minute" + s(minutes);
        }

        long days = hours / 24;
        hours = hours % 24;

        return "" + days + " day" + s(days) + " " +
                hours + " hour" + s(hours) + " and " +
                minutes + " minute" + s(minutes);
    }

    private static String s(long quantity) {
        if (quantity == 1) {
            return "";
        } else {
            return "s";
        }
    }

    /**
     * Helper function. Not public.
     *
     * @param text
     * @param format
     * @param timeZone
     * @return
     * @throws ParseException
     */
    static Date parse(String text, String format, TimeZone timeZone) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat(format);
        if (timeZone != null) {
            f.setTimeZone(timeZone);
        }

        return f.parse(text);
    }

}