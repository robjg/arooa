package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;

/**
 * A consumer of an {@link ArooaConfiguration} that can save it.
 */
@FunctionalInterface
public interface SaveOperation {

    void save(ArooaConfiguration arooaConfiguration) throws ArooaParseException;
}
