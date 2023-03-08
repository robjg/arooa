/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.FinalConvertlet;
import org.oddjob.arooa.utils.DateHelper;

import java.time.Instant;
import java.util.Date;

public class DateConvertlets implements ConversionProvider {

    public void registerWith(ConversionRegistry registry) {

        registry.register(Date.class, String.class,
                (FinalConvertlet<Date, String>) DateHelper::formatDateTime);

        registry.register(String.class, Date.class,
                from -> {
                    String stringValue = from.trim();
                    if (stringValue.length() == 0) {
                        return null;
                    } else {
                        return DateHelper.parseDateTime(stringValue);
                    }
                });

        registry.register(Long.class, Date.class,
                (FinalConvertlet<Long, Date>) Date::new);

        registry.register(Date.class, Long.class,
                Date::getTime);

        registry.register(Instant.class, Date.class, Date::from);

        registry.register(Date.class, Instant.class, Date::toInstant);
    }

}
