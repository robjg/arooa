package org.oddjob.arooa.registry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * An simple implementation of a {@link ComponentPool}.
 * <p>
 * This class is thread safe for the addition an removal of components. It is
 * not thread safe for configuration an saving. This is left to the
 * calling code. Oddjob for instance achieves thread safety for for these
 * operations by locking on State.
 * <p>
 *
 * @author rob
 */
public class SimpleComponentPool implements ComponentPool {

    private static final Logger logger = LoggerFactory.getLogger(SimpleComponentPool.class);

    private final AllWayIndex index = new AllWayIndex();

    @Override
    public void configure(Object component)
            throws ArooaConfigurationException {

        if (component == null) {
            throw new NullPointerException("No component.");
        }

        ArooaContext context = contextFor(component);
        if (context == null) {
            // quietly return. Component might not have been registered.
            return;
        }

        RuntimeConfiguration runtime = context.getRuntime();

        runtime.configure();
    }

    @Override
    public void save(Object component) throws ComponentPersistException {

        if (component == null) {
            throw new NullPointerException("No component.");
        }

        ComponentTrinity trinity = index.trinityFor(component);
        if (trinity == null) {
            // quietly return. Component might not have been registered.
            return;
        }

        ArooaSession session = trinity.getTheContext().getSession();

        ComponentPersister persister = session.getComponentPersister();

        if (persister == null) {
            return;
        }

        String id = index.idFor(trinity);

        if (id == null) {
            return;
        }

        persister.persist(id, trinity.getTheProxy(), session);
    }

    @Override
    public boolean remove(Object either) throws ComponentPersistException {
        Objects.requireNonNull(either, "No component.");

        ComponentTrinity trinity = null;
        synchronized (index) {
            trinity = index.trinityFor(either);
        }

        if (trinity == null) {
            // Used to throw an Exception here but a component might
            // not have fully initialised before being destroyed.
            // I.e. when a cut and paste fails.
            return false;
        }

        ArooaSession session = trinity.getTheContext().getSession();

        ComponentPersister persister = session.getComponentPersister();

        if (persister != null) {
            String id = index.idFor(trinity);

            if (id != null) {
                persister.remove(id, session);
            }
        }

        index.remove(trinity);

        trinity.getTheContext().getSession().getBeanRegistry().remove(
                trinity.getTheProxy());

        return true;
    }

    @Override
    public ArooaContext contextFor(Object either) {
        ComponentTrinity trinity = index.trinityFor(either);

        if (trinity == null) {
            return null;
        }

        return trinity.getTheContext();
    }

    @Override
    public ComponentTrinity trinityForContext(ArooaContext context) {
        return index.trinityForContext(context);
    }

    @Override
    public ComponentTrinity trinityFor(Object either) {
        return index.trinityFor(either);
    }

    @Override
    public ComponentTrinity trinityForId(String id) {
        synchronized (index) {
            return index.trinityForId(id);
        }
    }

    @Override
    public String getIdFor(Object either) {
        synchronized (index) {
            return index.idFor(
                    index.trinityFor(either));
        }
    }

    @Override
    public Iterable<ComponentTrinity> allTrinities() {
        synchronized (index) {
            return index.trinities();
        }
    }

    @Override
    public String registerComponent(ComponentTrinity componentTrinity, String id) {

        if (id == null) {
            return index.add(componentTrinity, null);
        }

        validateId(id);

        BeanRegistry registry =
                componentTrinity.getTheContext()
                                .getSession()
                                .getBeanRegistry();

        String newId = id;
        int count = 1;
        while (true) {
            Object existing = registry.lookup(newId);
            if (existing == null) {
                break;
            } else {
                newId = id + ++count;
            }
        }

        registry.register(
                newId, componentTrinity.getTheProxy());

        index.add(componentTrinity, newId);

        return newId;
    }

    private void validateId(String id) {
        if (id.length() == 0) {
            throw new IllegalArgumentException("Blank id is invalid.");
        }
    }

    static class AllWayIndex {

        /**
         * ComponentTrinities mapped by proxy object.
         */
        private final Map<Object, ComponentTrinity> proxiesTo =
                new HashMap<>();

        /**
         * ComponentTrinities mapped by the component.
         * Note this map maintains order added for iteration.
         */
        private final Map<Object, ComponentTrinity> componentsTo =
                new LinkedHashMap<>();

        /**
         * ComponentTrinities mapped by contexts.
         */
        private final Map<ArooaContext, ComponentTrinity> contextsTo =
                new HashMap<>();

        /**
         * Id's mapped by ComponentTrinity.
         */
        private final Map<ComponentTrinity, String> ids =
                new HashMap<>();

        /**
         * ComponentTrinities mapped by id.
         */
        private final Map<String, ComponentTrinity> trinities =
                new HashMap<>();

        synchronized String add(ComponentTrinity trinity, String id) {

            if (componentsTo.containsKey(trinity.getTheComponent())) {
                throw new IllegalStateException(
                        "Registered already: component " + trinity.getTheComponent());
            }

            if (proxiesTo.containsKey(trinity.getTheProxy())) {
                throw new IllegalStateException(
                        "Registered already proxy for : " +
                                trinity.getTheComponent());
            }

            if (proxiesTo.containsKey(trinity.getTheProxy())) {
                throw new IllegalStateException(
                        "Registered already context for: " +
                                trinity.getTheComponent());
            }
            if (trinities.containsKey(id)) {
                throw new IllegalStateException(
                        "Registered already id " + id + " for " +
                                trinity.getTheComponent());
            }

            componentsTo.put(trinity.getTheComponent(), trinity);
            proxiesTo.put(trinity.getTheProxy(), trinity);
            contextsTo.put(trinity.getTheContext(), trinity);

            if (id != null) {
                ids.put(trinity, id);
                trinities.put(id, trinity);
            }
            return id;
        }

        synchronized boolean contains(String id) {
            return trinities.containsKey(id);
        }

        synchronized Iterable<ComponentTrinity> trinities() {
            return componentsTo.values();
        }


        synchronized ComponentTrinity trinityFor(Object either) {
            if (proxiesTo.containsKey(either)) {
                return proxiesTo.get(either);
            }
            if (componentsTo.containsKey(either)) {
                return componentsTo.get(either);
            }
            return null;
        }

        synchronized ComponentTrinity trinityForContext(
                ArooaContext arooaContext) {
            return contextsTo.get(arooaContext);
        }

        synchronized void remove(ComponentTrinity trinity) {
            Objects.requireNonNull(trinity);

            // When is this not real?
            ComponentTrinity real =
                    componentsTo.remove(trinity.getTheComponent());

            if (real == null) {
                throw new IllegalStateException(
                        "Not registered " + trinity.getTheComponent());
            }

            proxiesTo.remove(real.getTheProxy());
            contextsTo.remove(real.getTheContext());

            String id = ids.remove(real);
            if (id != null) {
                trinities.remove(id);
            }
        }

        synchronized String idFor(ComponentTrinity trinity) {
            if (trinity == null) {
                return null;
            }


            return ids.get(trinity);
        }

        synchronized ComponentTrinity trinityForId(String id) {
            return trinities.get(id);
        }
    }
}
