/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaDescriptor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * 
 */
public class ArooaDescriptorBeanTest extends ArooaDescriptorTestBase {
	
	ArooaDescriptorBean test = new ArooaDescriptorBean();
	
	protected void setUp() throws ArooaParseException {
		
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
	
	
	public void testNamespaceIsAttribute() throws Exception {

		ArooaDescriptor descriptor = new LinkedDescriptor(
				new ArooaDescriptorDescriptor(),
				new StandardArooaDescriptor());

		ArooaBeanDescriptor beanDescriptor =
			descriptor.getBeanDescriptor(
					new SimpleArooaClass(ArooaDescriptorBean.class), 
					new BeanUtilsPropertyAccessor());
		
		BeanDescriptorHelper sort = new BeanDescriptorHelper(beanDescriptor);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				sort.getConfiguredHow("namespace"));
	}

}
