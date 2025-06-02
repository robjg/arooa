package org.oddjob.arooa.standard;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.ConfigurationDescriptorFactory;
import org.oddjob.arooa.deploy.LinkedDescriptor;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.*;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.*;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.io.Serializable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ComponentConfigurationCreatorTest {

    public static class DummyComponent implements Serializable {
        private static final long serialVersionUID = 20070701;

        String fruit;

        public void setFruit(String fruit) {
            this.fruit = fruit;
        }

        public String getFruit() {
            return this.fruit;
        }
    }

    private static class DummyProxy {
        Object wrapped;

        DummyProxy(Object wrapped) {
            this.wrapped = wrapped;
        }
    }

    private static class DummyComponentAccessor extends MockPropertyAccessor {
        @Override
        public BeanOverview getBeanOverview(Class<?> classId)
                throws ArooaException {

            assertEquals(DummyComponent.class, classId);

            BeanOverview beanOverview = mock(BeanOverview.class);
            doAnswer(invocationOnMock -> {
                String property = invocationOnMock.getArgument(0);
                if ("id".equals(property)) {
                    return false;
                }
                if ("fruit".equals(property)) {
                    return true;
                }
                throw new RuntimeException("Unexpected.");
            }).when(beanOverview).hasWriteableProperty(anyString());
            doReturn(String.class).when(beanOverview).getPropertyType("fruit");
            when(beanOverview.getProperties()).thenReturn(new String[]{"fruit"});

            return beanOverview;
        }

        @Override
        public ArooaClass getClassName(Object bean) {
            Class<?> cl = bean.getClass();
            return new SimpleArooaClass(cl);
        }

        @Override
        public void setSimpleProperty(Object bean, String name, Object value) throws ArooaException {
            assertThat(bean, instanceOf(DummyComponent.class));
            assertEquals("fruit", name);
            ((DummyComponent) bean).setFruit((String) value);
        }

        @Override
        public PropertyAccessor accessorWithConversions(ArooaConverter converter) {
            return this;
        }
    }

    private static class ComponentSession extends MockArooaSession {
        ArooaDescriptor arooaDescriptor;
        PropertyAccessor propertyAccessor;

        ComponentProxyResolver postProcessor = null;
        ComponentPersister componentPersister = null;

        String id;
        DummyComponent component;
        Object proxy;

        @Override
        public ComponentProxyResolver getComponentProxyResolver() {
            return postProcessor;
        }

        @Override
        public ComponentPersister getComponentPersister() {
            return componentPersister;
        }

        @Override
        public ComponentPool getComponentPool() {
            return new MockComponentPool() {
                @Override
                public String registerComponent(ComponentTrinity trinity, String id) {
                    ComponentSession.this.id = id;
                    ComponentSession.this.component = (DummyComponent) trinity.getTheComponent();
                    ComponentSession.this.proxy = trinity.getTheProxy();
                    return id;
                }

            };
        }

        @Override
        public ArooaTools getTools() {
            return new MockArooaTools() {

                @Override
                public PropertyAccessor getPropertyAccessor() {
                    if (propertyAccessor == null) {
                        throw new RuntimeException("Unexpected");
                    }
                    return propertyAccessor;
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
        public ArooaDescriptor getArooaDescriptor() {
            if (arooaDescriptor == null) {
                throw new RuntimeException("Unexpected");
            }
            return arooaDescriptor;
        }
    }

    private static class ParentContext extends MockArooaContext {

        Object componentCreated;

        ArooaSession session;

        RuntimeListener listener;

        public ParentContext(ArooaSession session) {
            this.session = session;
        }

        @Override
        public ArooaType getArooaType() {
            return ArooaType.COMPONENT;
        }

        @Override
        public RuntimeConfiguration getRuntime() {
            return new MockRuntimeConfiguration() {
                @Override
                public void addRuntimeListener(
                        RuntimeListener listener) {
                    ParentContext.this.listener = listener;
                }

                @Override
                public void setProperty(String name, Object value)
                        throws ArooaException {
                    assertNull(name);
                    componentCreated = value;
                }

                @Override
                public ArooaClass getClassIdentifier() {
                    return new SimpleArooaClass(DummyComponent.class);
                }
            };
        }

        @Override
        public ArooaSession getSession() {
            return session;
        }


        @Override
        public ConfigurationNode<ArooaContext> getConfigurationNode() {
            return new MockConfigurationNode() {
                @Override
                public int indexOf(ConfigurationNode<?> child) {
                    return 0;
                }
            };
        }
    }

    private static class ComponentArooaDescriptor extends MockArooaDescriptor {

        @Override
        public ArooaBeanDescriptor getBeanDescriptor(
                ArooaClass classIdentifier, PropertyAccessor accessor) {
            return null;
        }

        @Override
        public ElementMappings getElementMappings() {
            return new MappingsSwitch(new MockElementMappings() {
                @Override
                public ArooaClass mappingFor(ArooaElement element,
                                             InstantiationContext parentContext) {
                    if ("whatever".equals(element.getTag())) {
                        return new SimpleArooaClass(
                                DummyComponent.class);
                    }
                    return null;
                }
            }, null);
        }

        @Override
        public ClassResolver getClassResolver() {
            return new ClassLoaderClassResolver(
                    getClass().getClassLoader());
        }

    }

    private static class InterceptContext extends MockArooaContext {
        final RuntimeConfiguration runtime;
        final ArooaContext parent;

        InterceptContext(RuntimeConfiguration runtime,
                         ArooaContext parent) {
            this.runtime = runtime;
            this.parent = parent;
        }

        @Override
        public RuntimeConfiguration getRuntime() {
            return runtime;
        }


        @Override
        public ArooaSession getSession() {
            return parent.getSession();
        }

        @Override
        public ConfigurationNode<ArooaContext> getConfigurationNode() {
            return new MockConfigurationNode() {
            };
        }
    }

    private static class OurComponentProxyResolver implements ComponentProxyResolver {

        @Override
        public Object resolve(Object object, ArooaSession session) {
            return new DummyProxy(object);
        }

        @Override
        public Object restore(Object proxy, ArooaSession session) {
            assertThat(proxy, instanceOf(DummyProxy.class));
            return ((DummyProxy) proxy).wrapped;
        }
    }

    /**
     * Test creating a component from a class specification.
     */
    @Test
    public void testComponentFromClassCreate() throws ArooaException {

        String descriptorXML =
                "<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
                        " <components>" +
                        "  <arooa:bean-def element='whatever'" +
                        "      className='" + DummyComponent.class.getName() + "'>" +
                        "  </arooa:bean-def>" +
                        " </components>" +
                        "</arooa:descriptor>";

        ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
                new XMLConfiguration("XML", descriptorXML)).createDescriptor(
                getClass().getClassLoader());

        StandardArooaSession session = new StandardArooaSession(descriptor) {
            public ComponentProxyResolver getComponentProxyResolver() {
                return new OurComponentProxyResolver();
            }
        };

        MutableAttributes atts = new MutableAttributes();
        atts.set("class", DummyComponent.class.getName());
        atts.set("fruit", "Apples");
        ArooaElement element = new ArooaElement("bean", atts);

        // Create the runtime.

        ParentContext parentContext = new ParentContext(session);


        ComponentConfigurationCreator test = new ComponentConfigurationCreator();

        ComponentConfiguration result = test.onElement(
                element, parentContext);

        // check runtime created with the right runtime class.
        assertNotNull(result);
        assertEquals(DummyComponent.class,
                result.getWrappedObject().getClass());

        SimpleInstanceRuntime wrapper = new SimpleInstanceRuntime(
                result, parentContext);

        wrapper.setContext(new InterceptContext(
                wrapper, parentContext));

        wrapper.init();

        ComponentTrinity trinity = null;

        for (ComponentTrinity t :
                session.getComponentPool().allTrinities()) {
            if (trinity == null) {
                trinity = t;
            } else {
                throw new IllegalStateException("only one expected.");
            }
        }
        // check component registered.
        assertThat(trinity, Matchers.notNullValue());
        assertThat("The component", trinity.getTheComponent(), notNullValue());
        assertThat("The Proxy", trinity.getTheProxy(), notNullValue());
        assertThat(trinity.getTheProxy(), instanceOf(DummyProxy.class));

        // check wrapped object
        assertThat("Wrapped object is a proxy", result.getObjectToSet(), instanceOf(DummyProxy.class));

        // check parent context
        assertNotNull(parentContext.componentCreated);
        assertNotNull(parentContext.listener);
    }

    /**
     * Test creating a component from an element name.
     */
    @Test
    public void testComponentFromElementCreate() throws ArooaException {

        MutableAttributes attributes = new MutableAttributes();
        attributes.set("id", "dumdum");
        attributes.set("fruit", "Apples");
        ArooaElement element = new ArooaElement("whatever", attributes);

        ComponentSession session = new ComponentSession();
        session.propertyAccessor = new DummyComponentAccessor();

        ComponentArooaDescriptor descriptor = new ComponentArooaDescriptor();

        session.arooaDescriptor = new LinkedDescriptor(
                descriptor,
                new StandardArooaDescriptor());

        ParentContext parentContext = new ParentContext(session);

        ComponentConfigurationCreator test = new ComponentConfigurationCreator();

        // Create the runtime.
        ComponentConfiguration result = test.onElement(
                element, parentContext);

        // test runtime created with the correct runtime class.
        assertNotNull(result);
        assertEquals(DummyComponent.class,
                result.getWrappedObject().getClass());

        SimpleInstanceRuntime wrapper = new SimpleInstanceRuntime(
                result, parentContext);

        wrapper.setContext(new InterceptContext(
                wrapper, parentContext));

        wrapper.init();

        // test component registered.
        assertEquals("dumdum", session.id);
        assertThat("The component.", session.component, notNullValue());
        assertThat("The Proxy", session.proxy, notNullValue());
        assertThat(session.proxy, instanceOf(DummyComponent.class));
    }

    private static class OurComponentPersister extends MockComponentPersister {

        Object restore;

        @Override
        public Object restore(String id, ClassLoader loader, ArooaSession session) {
            assertEquals("dumdum", id);
            return restore;
        }
    }

    @Test
    public void testRestoreComponent() {

        MutableAttributes attributes = new MutableAttributes();
        attributes.set("id", "dumdum");
        attributes.set("fruit", "Apples");
        ArooaElement element = new ArooaElement("whatever", attributes);

        ComponentSession session = new ComponentSession();
        session.propertyAccessor = new DummyComponentAccessor();

        OurComponentPersister persister = new OurComponentPersister();
        persister.restore = new DummyComponent();

        session.componentPersister = persister;

        session.arooaDescriptor = new LinkedDescriptor(
                new ComponentArooaDescriptor(),
                new StandardArooaDescriptor());

        ParentContext parentContext = new ParentContext(session);

        ComponentConfigurationCreator test = new ComponentConfigurationCreator();

        // Create the runtime.
        ComponentConfiguration result = test.onElement(
                element, parentContext);

        // test runtime created with the correct runtime class.
        assertNotNull(result);
        assertEquals(DummyComponent.class,
                result.getWrappedObject().getClass());

        SimpleInstanceRuntime wrapper = new SimpleInstanceRuntime(
                result, parentContext);

        wrapper.setContext(new InterceptContext(
                wrapper, parentContext));

        wrapper.init();

        // test component registered.
        assertEquals("dumdum", session.id);
        assertThat("The component.", session.component, notNullValue());
        assertThat("The Proxy", session.proxy, notNullValue());
        assertThat(session.proxy, instanceOf(DummyComponent.class));
    }

    @Test
    public void testRestoreProxy() {

        String descriptorXML =
                "<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
                        " <components>" +
                        "  <arooa:bean-def element='whatever'" +
                        "      className='org.oddjob.arooa.standard.ComponentConfigurationCreatorTest$DummyComponent'>" +
                        "  </arooa:bean-def>" +
                        " </components>" +
                        "</arooa:descriptor>";

        ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
                new XMLConfiguration("XML", descriptorXML)).createDescriptor(
                getClass().getClassLoader());

        final OurComponentPersister persister = new OurComponentPersister();
        persister.restore = new DummyProxy(new DummyComponent());

        StandardArooaSession session = new StandardArooaSession(descriptor) {
            @Override
            public ComponentProxyResolver getComponentProxyResolver() {
                return new OurComponentProxyResolver();
            }

            @Override
            public ComponentPersister getComponentPersister() {
                return persister;
            }
        };

        MutableAttributes attributes = new MutableAttributes();
        attributes.set("id", "dumdum");
        attributes.set("fruit", "Apples");
        ArooaElement element = new ArooaElement("whatever", attributes);

        ParentContext parentContext = new ParentContext(session);

        ComponentConfigurationCreator test = new ComponentConfigurationCreator();

        // Create the runtime.
        ComponentConfiguration result = test.onElement(
                element, parentContext);

        // test runtime created with the correct runtime class.
        assertNotNull(result);
        assertEquals(DummyComponent.class,
                result.getWrappedObject().getClass());

        SimpleInstanceRuntime wrapper = new SimpleInstanceRuntime(
                result, parentContext);

        wrapper.setContext(new InterceptContext(
                wrapper, parentContext));

        wrapper.init();

        ComponentTrinity trinity = session.getComponentPool().trinityForId(
                "dumdum");

        // test component registered.
        assertThat("The component.", trinity.getTheComponent(), notNullValue());
        assertThat("The Proxy", trinity.getTheProxy(), notNullValue());
        assertThat(trinity.getTheProxy(), instanceOf(DummyProxy.class));
    }
}
