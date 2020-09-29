package org.oddjob.arooa.runtime;

import org.oddjob.arooa.registry.BeanRegistry;

import javax.script.Bindings;
import java.util.*;

/**
 * Provide script {@link Bindings} from an {@link org.oddjob.arooa.ArooaSession}'s
 * {@link BeanRegistry}.
 */
public class SessionBindings implements Bindings {

    private final BeanRegistry beanRegistry;

    public SessionBindings(BeanRegistry beanRegistry) {
        this.beanRegistry = Objects.requireNonNull(beanRegistry);
    }

    @Override
    public Object put(String name, Object value) {
        throw new UnsupportedOperationException("Read Only");
    }

    @Override
    public void putAll(Map<? extends String, ?> toMerge) {
        throw new UnsupportedOperationException("Read Only");
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public Object get(Object key) {
            return beanRegistry.lookup(key.toString());
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Read Only");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Read Only");
    }

    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection<Object> values() {
        return Collections.emptySet();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Collections.emptySet();
    }
}
