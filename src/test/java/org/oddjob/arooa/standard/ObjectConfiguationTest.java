package org.oddjob.arooa.standard;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.MockBeanRegistry;
import org.oddjob.arooa.runtime.*;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectConfiguationTest extends Assert {


    public static class SomeBean {
        private String fruit;

        public void setFruit(String fruit) {
            this.fruit = fruit;
        }
    }

    private class OurAttributeTestSession extends MockArooaSession {

        @Override
        public ArooaDescriptor getArooaDescriptor() {
            return new StandardArooaDescriptor();
        }

        @Override
        public ArooaTools getTools() {
            return new MockArooaTools() {
                @Override
                public PropertyAccessor getPropertyAccessor() {
                    return new BeanUtilsPropertyAccessor();
                }

                @Override
                public ExpressionParser getExpressionParser() {
                    return new StandardPropertyHelper();
                }

                @Override
                public ArooaConverter getArooaConverter() {
                    return new DefaultConverter();
                }

                @Override
                public Evaluator getEvaluator() {
                    return new PropertyFirstEvaluator();
                }
            };
        }

        @Override
        public BeanRegistry getBeanRegistry() {
            return new MockBeanRegistry() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> T lookup(String path, Class<T> required) {
                    assertEquals("Apple", path);
                    assertEquals(String.class, required);
                    return (T) "Orange";
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
    }

    private class AContext extends MockArooaContext {
        OurAttributeTestSession session = new OurAttributeTestSession();

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
                    assertThat(runtimeListener, is(listener));
                    runtimeListener = null;
                }
            };
        }
    }


    private class AnInstanceRuntime extends MockInstanceRuntime {
        Object value;

        public AnInstanceRuntime(InstanceConfiguration instance, ArooaContext parentContext) {
            super(instance, parentContext);
        }

        @Override
        ParentPropertySetter getParentPropertySetter() {
            return value -> AnInstanceRuntime.this.value = value;
        }
    }

    @Test
    public void testNonConstAttributes() {

        AContext parentContext = new AContext();

        MutableAttributes attrs = new MutableAttributes();
        attrs.set("fruit", "${Apple}");

        SomeBean ourBean = new SomeBean();

        ObjectConfiguration test = new ObjectConfiguration(
                new SimpleArooaClass(SomeBean.class),
                ourBean,
                attrs);

        AnInstanceRuntime instanceRuntime = new AnInstanceRuntime(test, parentContext);

        assertNull("No attribute", ourBean.fruit);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getSession())
                .thenReturn(parentContext.getSession());
        when(ourContext.getRuntime())
                .thenReturn(instanceRuntime);

        instanceRuntime.setContext(ourContext);

        test.init(instanceRuntime, ourContext);

        assertNull("No attribute because not constant.",
                   ourBean.fruit);

        test.listenerConfigure(instanceRuntime, ourContext);


        assertEquals("Runtime attribute set.", "Orange",
                     ourBean.fruit);

        assertThat(instanceRuntime.value, is(ourBean));
    }

    @Test
    public void testConstAttributes() {

        AContext parentContext = new AContext();

        MutableAttributes attrs = new MutableAttributes();
        attrs.set("fruit", "Apple");

        SomeBean ourBean = new SomeBean();

        ObjectConfiguration test = new ObjectConfiguration(
                new SimpleArooaClass(SomeBean.class),
                ourBean,
                attrs);

        AnInstanceRuntime instanceRuntime = new AnInstanceRuntime(test, parentContext);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getSession())
                .thenReturn(parentContext.getSession());
        when(ourContext.getRuntime())
                .thenReturn(instanceRuntime);

        instanceRuntime.setContext(ourContext);

        test.init(instanceRuntime, ourContext);

        assertEquals("Constant attribute set.", "Apple",
                     ourBean.fruit);

        test.listenerConfigure(instanceRuntime, ourContext);

        assertEquals("Still Constant attribute set.", "Apple",
                     ourBean.fruit);

    }

    public static class SomeBean2 {
        private String fruit;

        @ArooaText
        public void setFruit(String fruit) {
            this.fruit = fruit;
        }
    }

    private class TestTextContext extends MockArooaContext {

        RuntimeListener runtimeListener;

        OurAttributeTestSession session = new OurAttributeTestSession() {
            @Override
            public ArooaDescriptor getArooaDescriptor() {
                return new StandardArooaDescriptor();
            }
        };

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
    }

    @Test
    public void testAddNonConstText() {

        TestTextContext parentContext = new TestTextContext();

        SomeBean2 ourBean = new SomeBean2();

        ObjectConfiguration test = new ObjectConfiguration(
                new SimpleArooaClass(SomeBean2.class),
                ourBean,
                new MutableAttributes());

        AnInstanceRuntime instanceRuntime = new AnInstanceRuntime(test, parentContext);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getSession())
                .thenReturn(parentContext.getSession());
        when(ourContext.getRuntime())
                .thenReturn(instanceRuntime);

        instanceRuntime.setContext(ourContext);

        test.addText("${Apple}");

        assertNull("No attribute", ourBean.fruit);

        test.init(instanceRuntime, ourContext);

        assertNull("No attribute because not constant.",
                   ourBean.fruit);

        test.listenerConfigure(instanceRuntime, ourContext);

        assertEquals("Runtime attribute set.",
                     "Orange",
                     ourBean.fruit);
    }

    @Test
    public void testAddConstText() {

        TestTextContext parentContext = new TestTextContext();

        SomeBean ourBean = new SomeBean();

        ObjectConfiguration test = new ObjectConfiguration(
                new SimpleArooaClass(SomeBean2.class),
                ourBean,
                new MutableAttributes());

        AnInstanceRuntime instanceRuntime = new AnInstanceRuntime(test, parentContext);

        ArooaContext ourContext = mock(ArooaContext.class);
        when(ourContext.getSession())
                .thenReturn(parentContext.getSession());
        when(ourContext.getRuntime())
                .thenReturn(instanceRuntime);

        test.addText("Apple");

        test.init(instanceRuntime, parentContext);

        assertEquals("Constant attribute set.",
                     "Apple",
                     ourBean.fruit);

        test.listenerConfigure(instanceRuntime, parentContext);

        assertEquals("Still Constant attribute set.",
                     "Apple",
                     ourBean.fruit);
    }

}
