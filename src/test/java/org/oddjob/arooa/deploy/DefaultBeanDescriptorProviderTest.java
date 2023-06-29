package org.oddjob.arooa.deploy;

import org.junit.Test;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class DefaultBeanDescriptorProviderTest {

    enum Type {
        COX,
        GRANNY_SMITH,
        PINK_LADY
    }

    public static class Apple {

        public void setColour(String colour) {
        }

        public void setQuantity(int quantity) {
        }

        public void setType(Type type) {
        }

        public void setPicked(Date date) {
        }
    }


    @Test
    public void testIsAttribute() {

        DefaultBeanDescriptorProvider test =
                new DefaultBeanDescriptorProvider();

        BeanDescriptorBuilder builder = new BeanDescriptorBuilder(new SimpleArooaClass(Apple.class));

        test.findConfiguredHow(new BeanUtilsPropertyAccessor(), builder);

        ArooaBeanDescriptor beanDescriptor = builder.build();

        assertThat(beanDescriptor.getConfiguredHow("colour"),
                is(ConfiguredHow.ATTRIBUTE));

        assertThat(beanDescriptor.getConfiguredHow("quantity"),
                is(ConfiguredHow.ATTRIBUTE));

        assertThat(beanDescriptor.getConfiguredHow("type"),
                is(ConfiguredHow.ATTRIBUTE));

        assertThat(beanDescriptor.getConfiguredHow("picked"),
                is(ConfiguredHow.ELEMENT));

        try {
            beanDescriptor.getConfiguredHow("foo");
            assertThat("Should fail", false);
        }
        catch (ArooaPropertyException e) {
            assertThat(e.getMessage(), is("No writeable property [foo]"));
        }

        // Note that the defaults come from the BeanDescriptorHelper.

//        BeanDescriptorHelper sort = new BeanDescriptorHelper(beanDescriptor);
//
//        assertThat(sort.getConfiguredHow("colour"),
//                is(ConfiguredHow.ATTRIBUTE));
//
//        assertThat(sort.getConfiguredHow("quantity"),
//                is(ConfiguredHow.ATTRIBUTE));
//
//        assertThat(sort.getConfiguredHow("type"),
//                is(ConfiguredHow.ATTRIBUTE));
//
//        assertThat(sort.getConfiguredHow("date"),
//                is(ConfiguredHow.ELEMENT));
    }

    @Test
    public void validateAssumptionThatDefaultAnnotationsAreNotNull() {

        DefaultBeanDescriptorProvider test =
                new DefaultBeanDescriptorProvider();

        BeanDescriptorBuilder builder = new BeanDescriptorBuilder(new SimpleArooaClass(Apple.class));

        test.findConfiguredHow(new BeanUtilsPropertyAccessor(), builder);

        ArooaBeanDescriptor beanDescriptor = builder.build();

        ArooaAnnotations arooaAnnotations = beanDescriptor.getAnnotations();

        assertThat(arooaAnnotations, notNullValue());
    }

}
