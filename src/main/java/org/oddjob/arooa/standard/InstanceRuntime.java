package org.oddjob.arooa.standard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.ParsingSession;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.InstanceRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

/**
 * {@link RuntimeConfiguration} for an instance of something (a
 * component or value).
 * <p/>
 * This delegates the actual doing of the configuration to an
 * {@link InstanceConfiguration}.
 *
 * @author rob
 */
abstract class InstanceRuntime extends StandardRuntime
        implements InstanceRuntimeConfiguration {

    private final Map<ArooaElement, ArooaContext> childContexts =
            new HashMap<>();

    private final InstanceConfiguration instanceConfiguration;

    private final ArooaClass runtimeClass;

    /**
     * Added to a parent runtime to ensure configuration
     * and destruction events are passed down the hierarchy.
     */
    private final RuntimeListener runtimeListener =
            new RuntimeListener() {

                public void beforeInit(RuntimeEvent event)
                        throws ArooaConfigurationException {
                }

                public void afterInit(RuntimeEvent event)
                        throws ArooaConfigurationException {
                }

                public void beforeConfigure(RuntimeEvent event)
                        throws ArooaConfigurationException {
                    getInstanceConfiguration().listenerConfigure(
                            InstanceRuntime.this,
                            getContext());
                }

                public void afterConfigure(RuntimeEvent event)
                        throws ArooaConfigurationException {
                }

                public void beforeDestroy(RuntimeEvent event)
                        throws ArooaConfigurationException {

                    ArooaContext ourContext = getContext();
                    ArooaContext parentContext = getParentContext();
                    Objects.requireNonNull(ourContext,
                                           "Context not set");
                    Objects.requireNonNull(parentContext,
                                           "Parent Context not set");

                    getInstanceConfiguration().listenerDestroy(
                            InstanceRuntime.this,
                            ourContext);

                    int index = parentContext.getConfigurationNode().indexOf(
                            ourContext.getConfigurationNode());

                    // Destroy could be being called because parsing failed
                    // before the end element which is when a node is added.
                    if (index >= 0) {
                        parentContext.getConfigurationNode().removeChild(
                                index);
                    }

                    parentContext.getRuntime().removeRuntimeListener(runtimeListener);
                }

                public void afterDestroy(RuntimeEvent event)
                        throws ArooaConfigurationException {
                }
            };

    public InstanceRuntime(
            InstanceConfiguration instanceConfiguration,
            ArooaContext parentContext) {
        super(parentContext);

        Objects.requireNonNull(instanceConfiguration,
                               "No Instance.");

        this.instanceConfiguration = instanceConfiguration;
        this.runtimeClass = instanceConfiguration.getArooaClass();

        // Root instance doesn't have a parent runtime.
        Optional.ofNullable(parentContext.getRuntime())
                .ifPresent(runtime -> runtime.addRuntimeListener(
                        runtimeListener));
    }

    InstanceConfiguration getInstanceConfiguration() {
        return instanceConfiguration;
    }

    @Override
    public Object getWrappedInstance() {
        return instanceConfiguration.getWrappedObject();
    }

    void setContext(ArooaContext suggestedContext)
            throws ArooaConfigurationException {

        ArooaDescriptor descriptor = suggestedContext.getSession(
        ).getArooaDescriptor();

        PropertyAccessor accessor = suggestedContext.getSession(
        ).getTools().getPropertyAccessor();

        ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
                getClassIdentifier(), accessor);

        ArooaContext context = suggestedContext;

        if (beanDescriptor != null) {
            ParsingInterceptor interceptor = beanDescriptor.getParsingInterceptor();

            if (interceptor != null) {
                context = interceptor.intercept(context);
            }
        }

        // Context.getRuntime and this Runtime might be different by now.
        new LifecycleObligations().honour(
                InstanceRuntime.this, context);

        super.setContext(context);

        instanceConfiguration.contextAvailable(getContext());

        ParsingSession.addRollback(this::destroy);
    }

    @Override
    ArooaHandler getHandler() {

        return (element, parentContext) -> {

            ArooaContext propertyContext = childContexts.get(element);

            if (propertyContext == null) {

                PropertyOfInstanceHandler propertyOfInstanceHandler =
                        new PropertyOfInstanceHandler();

                propertyContext = propertyOfInstanceHandler.onStartElement(
                        element, parentContext);

                childContexts.put(element, propertyContext);
            }

            return propertyContext;
        };
    }


    public ArooaClass getClassIdentifier() {
        return runtimeClass;
    }

    abstract ParentPropertySetter getParentPropertySetter();

    public void init() throws ArooaConfigurationException {

        getInstanceConfiguration().init(
                this,
                getContext());
    }

    public void configure() throws ArooaConfigurationException {
        getInstanceConfiguration().configure(
                this,
                getContext());
    }

    public void destroy() throws ArooaConfigurationException {

        // If this is destroyed directly (by a cut) then remove listener.
        if (getParentContext().getRuntime() != null) {
            getParentContext().getRuntime().removeRuntimeListener(runtimeListener);
        }

        getInstanceConfiguration().destroy(
                this,
                getContext());
    }

    public void setProperty(String name, Object value)
            throws ArooaPropertyException {
        instanceConfiguration.setProperty(name, value, getContext());
    }

    public void setIndexedProperty(String name, int index, Object value)
            throws ArooaPropertyException {
        instanceConfiguration.setIndexedProperty(name, index, value, getContext());
    }

    public void setMappedProperty(String name, String key, Object value)
            throws ArooaPropertyException {
        instanceConfiguration.setMappedProperty(name, key, value, getContext());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + instanceConfiguration.getWrappedObject();
    }
}
