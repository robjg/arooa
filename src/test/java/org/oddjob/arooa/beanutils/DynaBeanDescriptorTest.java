package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.LazyDynaMap;
import org.junit.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaDescriptor;
import org.oddjob.arooa.utils.Pair;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DynaBeanDescriptorTest {

    @Test
    public void testDynaBeanInStandardDescriptor() {

        MagicBeanDefinition def = new MagicBeanDefinition();

        def.setElement("SnackBean");

        MagicBeanDescriptorProperty prop1 = new MagicBeanDescriptorProperty();
        prop1.setName("fruit");
        prop1.setType("java.lang.String");

        MagicBeanDescriptorProperty prop2 = new MagicBeanDescriptorProperty();
        prop2.setName("quantity");
        prop2.setType("java.lang.Integer");

        MagicBeanDescriptorProperty prop3 = new MagicBeanDescriptorProperty();
        prop3.setName("stuff");
        prop3.setType("java.lang.Object");

        def.setProperties(0, prop1);
        def.setProperties(1, prop2);
        def.setProperties(2, prop3);

        Pair<ArooaClass, ArooaBeanDescriptor> magicPair = def.createMagic(
                getClass().getClassLoader());

        ArooaClass arooaClass = magicPair.getLeft();

        BeanUtilsPropertyAccessor accessor =
                new BeanUtilsPropertyAccessor();

        StandardArooaDescriptor test = new StandardArooaDescriptor();

        ArooaBeanDescriptor result = test.getBeanDescriptor(
                arooaClass, accessor);

        assertThat(result, notNullValue());

        assertThat(result.getConfiguredHow("fruit"), is(ConfiguredHow.ATTRIBUTE));

        assertThat(result.getConfiguredHow("quantity"), is(ConfiguredHow.ATTRIBUTE));

        assertThat(result.getConfiguredHow("stuff"), is(ConfiguredHow.ELEMENT));
    }

    @Test
    public void testMutableDynaBeanInStandardDescriptor() {

        LazyDynaMap dynaBean = new LazyDynaMap();

        BeanUtilsPropertyAccessor accessor =
                new BeanUtilsPropertyAccessor();

        StandardArooaDescriptor test = new StandardArooaDescriptor();

        ArooaClass arooaClass = accessor.getClassName(dynaBean);

        ArooaBeanDescriptor result = test.getBeanDescriptor(
                arooaClass, accessor);

        assertThat(result, notNullValue());

        try {
            result.getConfiguredHow("anything");
            assertThat("Should fail", false);
        }
        catch(ArooaPropertyException e) {
            assertThat(e.getMessage(), containsString("anything"));

        }
    }
}
