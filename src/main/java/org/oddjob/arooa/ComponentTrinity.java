package org.oddjob.arooa;

import org.oddjob.arooa.parsing.ArooaContext;

import java.util.Objects;

/**
 * Encapsulate the three things that are the bases of the
 * component framework.
 *
 * @author rob
 */
public class ComponentTrinity {

    /**
     * The raw bean component.
     */
    private final Object theComponent;

    /**
     * The thing that wraps it.
     */
    private final Object theProxy;

    /**
     * The context in which it was created.
     */
    private final ArooaContext theContext;

    public ComponentTrinity(Object theComponent,
                            Object theProxy,
                            ArooaContext theContext) {

        Objects.requireNonNull(theComponent, "No Component");
        Objects.requireNonNull(theProxy, "No Proxy");
        Objects.requireNonNull(theContext, "No Context");

        this.theComponent = theComponent;
        this.theProxy = theProxy;
        this.theContext = theContext;
    }

    /**
     * Getter for component.
     *
     * @return The component. Never null.
     */
    public Object getTheComponent() {
        return theComponent;
    }

    /**
     * Getter for proxy.
     *
     * @return The proxy. Never null.
     */
    public Object getTheProxy() {
        return theProxy;
    }

    /**
     * Getter for context.
     *
     * @return The context. Never null.
     */
    public ArooaContext getTheContext() {
        return theContext;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", component=" + theComponent;
    }
}
