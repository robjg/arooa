package org.oddjob.arooa.standard;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockBeanRegistry;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentConfigurationTest {

    private static class ATools extends MockArooaTools {

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

    private static class ASession extends MockArooaSession {
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
                    assertThat(id, is("anid"));
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
                    assertThat("Value to replace.", path, is("To be replaced"));
                    assertThat(required, is(String.class));
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

    private static class AContext extends MockArooaContext {
        final ASession session = new ASession();

        @SuppressWarnings("unchecked")
        final ConfigurationNode<ArooaContext> configurationNode =
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
                    assertThat(runtimeListener, is(listener));
                    runtimeListener = null;
                }
            };
        }

        @Override
        public ConfigurationNode<ArooaContext> getConfigurationNode() {
            return configurationNode;
        }
    }

    private static class ProxyObject {

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

    private static class AnInstanceRuntime extends MockInstanceRuntime {

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
        assertThat("Registered",
                parentContext.session.trinity.getTheComponent(), is(object));
        assertThat("Registered",
                parentContext.session.trinity.getTheProxy(), is(proxy));

        assertThat("Property not set", instanceRuntime.value, nullValue());

        test.init(instanceRuntime, ourContext);

        assertThat("Property not set", instanceRuntime.value, notNullValue());

        assertThat("Parent property set", instanceRuntime.value, is(proxy));

        assertThat("Property not set", object.colour, nullValue());

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

        assertThat("Property not set", instanceRuntime.value, nullValue());

        test.init(instanceRuntime, ourContext);

        assertThat("Property not set", instanceRuntime.value, notNullValue());

        assertThat(object.getColour(), is("red"));
    }

    private static class AnInstanceRuntime2 extends MockInstanceRuntime {

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

        assertThat("Property not set", theObject.colour, nullValue());

        ArooaContext ourContext = mock(ArooaContext.class);

        test.listenerConfigure(
                new AnInstanceRuntime2(test, parentContext), ourContext);

        assertThat("Property not set", theObject.colour, nullValue());
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

        assertThat("Property not set", theObject.getColour(), nullValue());

        AnInstanceRuntime instanceRuntime =
                new AnInstanceRuntime(test, parentContext);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getRuntime()).thenReturn(instanceRuntime);
        when(ourContext.getSession()).thenReturn(parentContext.getSession());

        test.configure(instanceRuntime, ourContext);

        assertThat(theObject.getColour(), is("red"));
    }
}
