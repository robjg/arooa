package org.oddjob.arooa.standard;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.*;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockBeanRegistry;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentConfigurationTest extends Assert {

    private class ATools extends MockArooaTools {

        @Override
        public ArooaConverter getArooaConverter() {
            return new DefaultConverter();
        }

        @Override
        public PropertyAccessor getPropertyAccessor() {
            return new BeanUtilsPropertyAccessor();
        }

        @Override
        public ExpressionParser getExpressionParser() {
            return new StandardPropertyHelper();
        }

        @Override
        public Evaluator getEvaluator() {
            return new PropertyFirstEvaluator();
        }

    }

    private class ASession extends MockArooaSession {
        ATools tools = new ATools();

        ComponentTrinity trinity;

        @Override
        public ArooaDescriptor getArooaDescriptor() {
            return new StandardArooaDescriptor();
        }

        @Override
        public ComponentPool getComponentPool() {
            return new MockComponentPool() {
                @Override
                public String registerComponent(ComponentTrinity trinity, String id) {
                    assertEquals("anid", id);
                    assertThat(trinity, notNullValue());

                    if (ASession.this.trinity != null) {
                        throw new RuntimeException("Registering twice??");
                    }

                    ASession.this.trinity = trinity;
                    return id;
                }


                @Override
                public boolean remove(Object component) {
                    assertThat(trinity.getTheProxy(), is(component));
                    ASession.this.trinity = null;
                    return true;
                }
            };
        }

        @Override
        public BeanRegistry getBeanRegistry() {
            return new MockBeanRegistry() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> T lookup(String path, Class<T> required) {
                    assertEquals("Value to replace.", "To be replaced", path);
                    assertEquals(String.class, required);
                    return (T) "red";
                }
            };
        }

        @Override
        public PropertyManager getPropertyManager() {
            return new MockPropertyManager() {
                @Override
                public String lookup(String propertyName) {
                    return null;
                }
            };
        }

        @Override
        public ArooaTools getTools() {
            return tools;
        }
    }

    private class AContext extends MockArooaContext {
        final ASession session = new ASession();

        final ConfigurationNode configurationNode =
                mock(ConfigurationNode.class);

        RuntimeListener runtimeListener;

        {
            when(configurationNode.indexOf(any(ConfigurationNode.class)))
                    .thenReturn(-1);
        }


        @Override
        public ArooaSession getSession() {
            return session;
        }

        @Override
        public RuntimeConfiguration getRuntime() {
            return new MockRuntimeConfiguration() {
                @Override
                public ArooaClass getClassIdentifier() {
                    return new SimpleArooaClass(
                            String.class);
                }

                @Override
                public void addRuntimeListener(RuntimeListener listener) {
                    assertThat(listener, notNullValue());
                    assertThat(runtimeListener, nullValue());
                    runtimeListener = listener;
                }

                @Override
                public void removeRuntimeListener(RuntimeListener listener) {
                    assertThat(listener, notNullValue());
                    assertThat(runtimeListener, is( listener ));
                    runtimeListener = null;
                }
            };
        }

        @Override
        public ConfigurationNode getConfigurationNode() {
            return configurationNode;
        }
    }

    private class ProxyObject {

    }

    public static class TheObject {

        String colour;

        public String getColour() {
            return colour;
        }

        public void setColour(String colour) {
            this.colour = colour;
        }
    }

    private class AnInstanceRuntime extends MockInstanceRuntime {

        Object value;

        public AnInstanceRuntime(InstanceConfiguration instance, ArooaContext parentContext) {
            super(instance, parentContext);
        }

        @Override
        ParentPropertySetter getParentPropertySetter() {
            return value -> {
                // Test registered.
                ASession ourSess = (ASession) getParentContext().getSession();

                AnInstanceRuntime.this.value = value;
            };
        }

    }

    @Test
    public void testInit() {

        AContext parentContext = new AContext();

        TheObject object = new TheObject();

        ProxyObject proxy = new ProxyObject();

        MutableAttributes attrs = new MutableAttributes();
        attrs.set("id", "anid");
        attrs.set("colour", "${To be replaced}");

        ComponentConfiguration test = new ComponentConfiguration(
                new SimpleArooaClass(object.getClass()),
                object,
                proxy,
                attrs);

        AnInstanceRuntime instanceRuntime = new AnInstanceRuntime(
                test, parentContext);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getSession())
               .thenReturn(parentContext.getSession());
        when(ourContext.getRuntime())
                .thenReturn(instanceRuntime);

        // Check component registered after context set.
        assertThat("Component Should not be registered",
                   parentContext.session.trinity,
                   nullValue());

        instanceRuntime.setContext(ourContext);

        // Check component registered after context set.
        assertEquals("Registered", object,
                     parentContext.session.trinity.getTheComponent());
        assertEquals("Registered", proxy,
                     parentContext.session.trinity.getTheProxy());

        assertNull("Property not set", instanceRuntime.value);

        test.init(instanceRuntime, ourContext);

        assertNotNull("Property not set", instanceRuntime.value);

        assertEquals("Parent property set", proxy, instanceRuntime.value);

        assertNull("Property not set",
                   object.colour);

        RuntimeConfiguration parentRuntime = mock(RuntimeConfiguration.class);

        parentContext.runtimeListener.beforeDestroy(
                new RuntimeEvent(parentRuntime));

        assertThat(parentContext.runtimeListener, nullValue());
    }

    @Test
    public void testInitConstantProperty() {

        AContext parentContext = new AContext();

        TheObject object = new TheObject();

        ProxyObject proxy = new ProxyObject();

        MutableAttributes attrs = new MutableAttributes();
        attrs.set("id", "anid");
        attrs.set("colour", "red");

        ComponentConfiguration test = new ComponentConfiguration(
                new SimpleArooaClass(object.getClass()),
                object,
                proxy,
                attrs);

        AnInstanceRuntime instanceRuntime = new AnInstanceRuntime(
                test, parentContext);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getSession())
                .thenReturn(parentContext.getSession());
        when(ourContext.getRuntime())
                .thenReturn(instanceRuntime);

        instanceRuntime.setContext(ourContext);

        assertNull("Property not set", instanceRuntime.value);

        test.init(instanceRuntime, ourContext);

        assertNotNull("Property not set", instanceRuntime.value);

        assertThat(object.getColour(), is("red"));
    }

    private class AnInstanceRuntime2 extends MockInstanceRuntime {

        public AnInstanceRuntime2(InstanceConfiguration instance,
                                  ArooaContext parentContext) {
            super(instance, parentContext);
        }

        @Override
        ParentPropertySetter getParentPropertySetter() {
            return null;
        }
    }

    @Test
    public void testListenerConfigure() {

        AContext parentContext = new AContext();

        MutableAttributes attrs = new MutableAttributes();
        attrs.set("colour", "${To be replaced}");

        TheObject theObject = new TheObject();

        ComponentConfiguration test = new ComponentConfiguration(
                new SimpleArooaClass(TheObject.class),
                theObject,
                new ProxyObject(),
                attrs);

        assertNull("Property not set", theObject.colour);

        ArooaContext ourContext = mock(ArooaContext.class);

        test.listenerConfigure(
                new AnInstanceRuntime2(test, parentContext), ourContext);

        assertNull("Property not set", theObject.colour);
    }

    @Test
    public void testConfigure() {

        AContext parentContext = new AContext();

        MutableAttributes attrs = new MutableAttributes();
        attrs.set("colour", "${To be replaced}");

        TheObject theObject = new TheObject();

        ComponentConfiguration test = new ComponentConfiguration(
                new SimpleArooaClass(TheObject.class),
                theObject,
                new ProxyObject(),
                attrs);

        assertNull("Property not set", theObject.getColour());

        AnInstanceRuntime instanceRuntime =
                new AnInstanceRuntime(test, parentContext);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getRuntime()).thenReturn(instanceRuntime);
        when(ourContext.getSession()).thenReturn(parentContext.getSession());

        test.configure(instanceRuntime, ourContext);

        assertThat(theObject.getColour(), is("red"));
    }
}
