package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.FinalConvertlet;
import org.oddjob.arooa.utils.DateTimeHelper;

import java.time.Instant;

/**
 * Conversions for new {@code java.time} types.
 *
 * TODO: Add Local and Zoned Times
 */
public class DateTimeConvertlets implements ConversionProvider {

    @Override
    public void registerWith(ConversionRegistry registry) {

        registry.register(String.class, Instant.class,
                ((FinalConvertlet<String, Instant>) DateTimeHelper::parseDateTime));

        registry.register(Instant.class, String.class, Instant::toString);

        registry.register(Long.class, Instant.class,
                (FinalConvertlet<Long, Instant>) Instant::ofEpochMilli);

        registry.register(Instant.class, Long.class,
                Instant::toEpochMilli);

    }
}
