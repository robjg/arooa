package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component configuration behaviour. Components are self configuring, they
 * should ignore requests to configure when the parent is being configured.
 *
 * @author rob
 * @see ObjectConfiguration
 */
class ComponentConfiguration extends InstanceConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(
            ComponentConfiguration.class);

    private final AttributeSetter attributeSetter;

    private String id;

    private final InjectionStrategy injectionStrategy = new InjectionStrategy() {

        boolean parentSet = false;

        public void init(ParentPropertySetter parentPropertySetter)
                throws ArooaPropertyException {
            parentPropertySetter.parentSetProperty(
                    getObjectToSet());
            parentSet = true;
        }

        public void configure(ParentPropertySetter parentPropertySetter) {
        }

        public void destroy(ParentPropertySetter parentPropertySetter)
                throws ArooaPropertyException {
            if (parentSet) {
                parentPropertySetter.parentSetProperty(
                        null);
                parentSet = false;
            }
        }
    };

    private final Object proxy;

    private class NewAttributes implements ArooaAttributes {
        private final ArooaAttributes delegate;

        private NewAttributes(ArooaAttributes delegate) {
            this.delegate = delegate;
        }

        @Override
        public String get(String name) {
            if (ArooaConstants.ID_PROPERTY.equals(name)) {
                return id;
            } else {
                return delegate.get(name);
            }
        }

        @Override
        public String[] getAttributeNames() {
            return delegate.getAttributeNames();
        }
    }

    ComponentConfiguration(
            ArooaClass arooaClass,
            Object wrappedObject,
            Object proxy,
            ArooaAttributes attributes) {
        super(arooaClass, wrappedObject);

        this.proxy = proxy;

        this.id = attributes.get(
                ArooaConstants.ID_PROPERTY);

        this.attributeSetter = new AttributeSetter(
                this,
                new NewAttributes(attributes));

        this.attributeSetter.addOptionalAttribute(
                ArooaConstants.ID_PROPERTY);

        this.attributeSetter.addInitAttribute(
                ArooaConstants.NAME_PROPERTY);
    }

    @Override
    AttributeSetter getAttributeSetter() {
        return attributeSetter;
    }

    String getId() {
        return id;
    }

    @Override
    Object getObjectToSet() {
        return proxy;
    }

    InjectionStrategy injectionStrategy() {
        return injectionStrategy;
    }

    @Override
    public void contextAvailable(ArooaContext context) {

        // A component is registered as soon a possible which is when
        // we have the context. This is because children might want to
        // access fixed properties of parent, as is often the case in
        // the name attribute.

        this.id = context.getSession()
                         .getComponentPool()
                         .registerComponent(
                                 new ComponentTrinity(
                                         getWrappedObject(), proxy, context),
                                 getId());

    }

    @Override
    void init(InstanceRuntime instanceRuntime,
              ArooaContext context)
            throws ArooaConfigurationException {

        instanceRuntime.fireBeforeInit();

        internalInit(context);

        instanceRuntime.fireAfterInit();

        injectionStrategy().init(instanceRuntime.getParentPropertySetter());
    }

    @Override
    void configure(InstanceRuntime ourWrapper,
                   ArooaContext context)
            throws ArooaConfigurationException {
        ourWrapper.fireBeforeConfigure();

        internalConfigure(context);

        ourWrapper.fireAfterConfigure();

        injectionStrategy().configure(ourWrapper.getParentPropertySetter());
    }

    @Override
    void listenerConfigure(InstanceRuntime ourWrapper,
                           ArooaContext context)
            throws ArooaException {
        // don't listen to parents
    }

    @Override
    void destroy(InstanceRuntime ourWrapper,
                 ArooaContext context)
            throws ArooaConfigurationException {
        doDestroy(ourWrapper, context);
    }

    @Override
    void listenerDestroy(InstanceRuntime ourRuntime,
                         ArooaContext context)
            throws ArooaConfigurationException {
        doDestroy(ourRuntime, context);
    }

    private void doDestroy(InstanceRuntime ourRuntime,
                           ArooaContext context)
            throws ArooaConfigurationException {

        ourRuntime.fireBeforeDestroy();

        try {
            context.getSession().getComponentPool().remove(
                    proxy);
        } catch (ComponentPersistException e) {
            logger.warn("Failed removing component " + proxy +
                                " from pool.", e);
        }

        injectionStrategy().destroy(ourRuntime.getParentPropertySetter());

        ourRuntime.fireAfterDestroy();
    }
}
