package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.utils.DateTimeHelper;

import java.time.Duration;
import java.time.Instant;

/**
 * Conversions for new {@code java.time} types.
 *
 * TODO: Add Local and Zoned Times
 */
public class DateTimeConvertlets implements ConversionProvider {

    /**
     * @oddjob.conversion Uses the Standard format for a Java {@link Duration}.
     */
    static class StringToDuration implements Convertlet<String, Duration> {

        @Override
        public Duration convert(String from) {
            return Duration.parse(from);
        }
    }

    /**
     * @oddjob.conversion From a {@link Duration} to its parsable String format.
     */
    static class DurationToString implements Convertlet<Duration, String> {

        @Override
        public String convert(Duration from) throws ArooaConversionException {
            return from.toString();
        }
    }

    @Override
    public void registerWith(ConversionRegistry registry) {

        registry.register(String.class, Instant.class,
                ((FinalConvertlet<String, Instant>) DateTimeHelper::parseDateTime));

        registry.register(Instant.class, String.class, Instant::toString);

        registry.register(Long.class, Instant.class,
                (FinalConvertlet<Long, Instant>) Instant::ofEpochMilli);

        registry.register(Instant.class, Long.class,
                Instant::toEpochMilli);

        registry.register(String.class, Duration.class,
                new StringToDuration());

        registry.register(Duration.class, String.class,
                new DurationToString());
    }
}
