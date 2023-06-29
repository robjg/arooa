/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.deploy;

import org.junit.Before;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaDescriptor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * 
 */
public class ArooaDescriptorBeanTest extends ArooaDescriptorTestBase {
	
	ArooaDescriptorBean test = new ArooaDescriptorBean();
	
   @Before
   public void setUp() throws ArooaParseException {
		
		ArooaConfiguration config = new XMLConfiguration(
				"Descriptor",
				ArooaDescriptorBeanTest.class.getResourceAsStream(
						"ArooaDescriptorBeanTest.xml"));
		
		StandardArooaParser parser = 
			new StandardArooaParser(test, 
					new ArooaDescriptorDescriptor());
		
		parser.parse(config);
		
		ArooaSession session = parser.getSession() ;
		
		session.getComponentPool().configure(test);
	}
	
	@Override
	ArooaDescriptor getTest(ClassLoader classLoader) {
		return test.createDescriptor(classLoader);
	}
	
	
   @Test
	public void testNamespaceIsAttribute() {

		ArooaDescriptor descriptor = new LinkedDescriptor(
				new ArooaDescriptorDescriptor(),
				new StandardArooaDescriptor());

		ArooaBeanDescriptor beanDescriptor =
			descriptor.getBeanDescriptor(
					new SimpleArooaClass(ArooaDescriptorBean.class), 
					new BeanUtilsPropertyAccessor());
		
		assertThat(beanDescriptor.getConfiguredHow("namespace"), is(ConfiguredHow.ATTRIBUTE));
	}

}
