package org.oddjob.arooa.registry;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.MockArooaConverter;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class FullPathLookupTest {

    public static class Fruit {

        public String getColour() {
            return "red";
        }
    }

    static class OurSession extends MockArooaSession {

        BeanRegistry registry = new SimpleBeanRegistry();

        @Override
        public BeanRegistry getBeanRegistry() {
            return registry;
        }

        @Override
        public ArooaTools getTools() {
            return new MockArooaTools() {
                @Override
                public PropertyAccessor getPropertyAccessor() {
                    return new BeanUtilsPropertyAccessor();
                }
            };
        }
    }

    @Test
    public void testRegistryLookup() throws ArooaPropertyException {

        OurSession session = new OurSession();

        session.registry.register("fruit", new Fruit());

        BeanDirectory lookup = session.getBeanRegistry();

        assertNotNull(lookup.lookup("fruit"));
        assertNotNull(lookup.lookup("fruit/"));

        // I think this should work
//		assertNotNull(lookup.lookup("/fruit"));
    }


    static class Component extends MockBeanDirectoryOwner {
        BeanDirectory directory;

        public BeanDirectory provideBeanDirectory() {
            return directory;
        }
    }

    @Test
    public void testNestedLookup() {


        SimpleBeanRegistry test = new SimpleBeanRegistry(
                mock(PropertyAccessor.class),
                new MockArooaConverter());

        Component c1 = new Component();

        test.register("outer", c1);

        OurSession session = new OurSession();

        session.registry.register("fruit", new Fruit());

        BeanDirectory lookup = session.getBeanRegistry();

        assertNotNull(lookup.lookup("fruit"));
        assertNotNull(lookup.lookup("fruit/"));

        // I think this should work
//		assertNotNull(lookup.lookup("/fruit"));
    }


    @Test
    public void testFullLookup() {

        OurSession session = new OurSession();

        session.registry.register("fruit", new Fruit());

        BeanDirectory lookup = session.getBeanRegistry();

        assertEquals("red", lookup.lookup("fruit.colour"));
    }
}
