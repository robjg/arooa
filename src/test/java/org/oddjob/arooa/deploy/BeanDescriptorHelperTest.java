package org.oddjob.arooa.deploy;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.xml.XMLConfiguration;

public class BeanDescriptorHelperTest extends Assert {

    @Test
    public void testElement() {

        BeanDescriptorHelper helper = new BeanDescriptorHelper(null);

        assertEquals(ConfiguredHow.ELEMENT, helper.getConfiguredHow("apples"));

        assertTrue(helper.isElement("apples"));
    }

    static class OurBeanDescriptor extends MockArooaBeanDescriptor {

        @Override
        public ConfiguredHow getConfiguredHow(String property) {
            return null;
        }
    }

    @Test
    public void testElement2() {

        BeanDescriptorHelper helper = new BeanDescriptorHelper(
                new OurBeanDescriptor());

        assertEquals(ConfiguredHow.ELEMENT, helper.getConfiguredHow("apples"));

        assertTrue(helper.isElement("apples"));
    }

    public static class MyBean {

    	private String colour;

		public String getColour() {
			return colour;
		}

		public void setColour(String colour) {
			this.colour = colour;
		}
	}

    @Test
    public void testAnnotatedProperty() {

        ClassLoader classLoader = getClass().getClassLoader();

        ArooaDescriptor descriptor =
                new ConfigurationDescriptorFactory(
                        new XMLConfiguration(
                                "org/oddjob/arooa/deploy/BeanDescriptorHelperTest.xml",
                                classLoader)).createDescriptor(
                        classLoader);

		BeanDescriptorHelper helper = new BeanDescriptorHelper(
				descriptor.getBeanDescriptor(new SimpleArooaClass(MyBean.class),
						new BeanUtilsPropertyAccessor()));

		MatcherAssert.assertThat(helper.isText("colour"),
				Matchers.is(true));
    }
}
