package org.oddjob.arooa.beanutils;

import org.junit.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.utils.Pair;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MagicBeanDefinitionTest {

    @Test
    public void testCreateMagic() {

        MagicBeanDefinition def = new MagicBeanDefinition();

        def.setElement("SnackBean");

        MagicBeanDescriptorProperty prop1 = new MagicBeanDescriptorProperty();
        prop1.setName("fruit");
        prop1.setType("java.lang.String");

        MagicBeanDescriptorProperty prop2 = new MagicBeanDescriptorProperty();
        prop2.setName("quantity");
        prop2.setType("java.lang.Integer");

        def.setProperties(0, prop1);
        def.setProperties(1, prop2);

        Pair<ArooaClass, ArooaBeanDescriptor> magicPair  = def.createMagic(
                getClass().getClassLoader());

        ArooaClass arooaClass = magicPair.getLeft();

        PropertyAccessor accessor = new BeanUtilsPropertyAccessor();

        BeanOverview overview = arooaClass.getBeanOverview(accessor);

        String[] properties = overview.getProperties();

        assertThat(properties, is(new String[]{"fruit", "quantity"}));

        assertThat(overview.getPropertyType("fruit"), is(String.class));
        assertThat(overview.getPropertyType("quantity"), is(Integer.class));
    }

    @Test
    public void whenConfiguredSetThenUsedAsExpected() {

        MagicBeanDefinition def = new MagicBeanDefinition();

        def.setElement("SomeBean");

        MagicBeanDescriptorProperty prop1 = new MagicBeanDescriptorProperty();
        prop1.setName("file");
        prop1.setType("java.io.File");
        prop1.setConfigured(MagicBeanDescriptorProperty.PropertyType.ATTRIBUTE);

        def.setProperties(0, prop1);

        Pair<ArooaClass, ArooaBeanDescriptor> magicPair = def.createMagic(getClass().getClassLoader());

        ArooaBeanDescriptor descriptor = magicPair.getRight();

        assertThat(descriptor.getConfiguredHow("file"), is(ConfiguredHow.ATTRIBUTE));
    }
}
