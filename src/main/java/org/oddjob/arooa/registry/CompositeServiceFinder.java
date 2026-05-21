package org.oddjob.arooa.registry;

import java.lang.reflect.Type;

/**
 * A {@link ServiceFinder} that combines the results of other
 * {@code ServiceFinder}s.
 *
 * @author rob
 */
public class CompositeServiceFinder implements ServiceFinder {

    private final ServiceFinder[] finders;

    public CompositeServiceFinder(ServiceFinder... finders) {
        this.finders = finders;
    }

    @Override
    public <T> T find(Type cl, String flavour) {
        for (ServiceFinder finder : finders) {
            T service = finder.find(cl, flavour);
            if (service != null) {
                return service;
            }
        }
        return null;
    }
}
