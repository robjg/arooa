package org.oddjob.arooa.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;

public class BeanDefinitionsTest extends Assert {

    public interface Fruit {

    }

    public static class Apple implements Fruit {

    }

    @Test
    public void testIsBeanDefinition() {

        BeanDefinitionBean beanDef = new BeanDefinitionBean();
        beanDef.setClassName(Apple.class.getName());
        beanDef.setElement("apple");

        ArooaDescriptorBean factory = new ArooaDescriptorBean();
        factory.setValues(0, beanDef);

        ArooaDescriptor descriptor = factory.createDescriptor(
                getClass().getClassLoader());

        ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
                new SimpleArooaClass(Apple.class),
                new BeanUtilsPropertyAccessor());

        assertNotNull(beanDescriptor);

        beanDef.setProperties(
                0, new PropertyDefinitionBean(
                        "description",
                        PropertyDefinitionBean.PropertyType.TEXT));

        descriptor = factory.createDescriptor(
                getClass().getClassLoader());

        beanDescriptor = descriptor.getBeanDescriptor(
                new SimpleArooaClass(Apple.class),
                new BeanUtilsPropertyAccessor());

        assertEquals("description", beanDescriptor.getTextProperty());
    }
}
