package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.deploy.PropertyDefinition.PropertyType;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.deploy.annotations.ArooaElement;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.deploy.annotations.ArooaInterceptor;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ArooaDescriptorBeanMoreTest extends TestCase {

	public static class OurBean {
		
		public void setFruit(String fruit) {
		}
	}
	
	public void testEmptyBeanDescriptor() {
		
		ArooaDescriptorBean test = new ArooaDescriptorBean();
		
		ArooaDescriptor descriptor = test.createDescriptor(getClass().getClassLoader());
		
		assertNotNull(descriptor.getElementMappings());
		
		ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				new SimpleArooaClass(OurBean.class), null);
		
		assertNull(beanDescriptor);
	}
	
	public void testNoClassName() {
		ArooaDescriptorBean test = new ArooaDescriptorBean();

		ArooaDescriptor descriptor = test.createDescriptor(
				getClass().getClassLoader());
		
		assertNotNull(descriptor.getElementMappings());
	}
	
	public void testBeanDescriptorQuery() {
		
		ArooaDescriptorBean test = new ArooaDescriptorBean();

		BeanDefinition def = new BeanDefinition();
		def.setClassName(OurBean.class.getName());
		def.setElement("snack");
		
		PropertyDefinition prop = new PropertyDefinition(
				"stuff", PropertyType.COMPONENT);
		
		def.setProperties(0, prop);
		
		test.setComponents(0, def);
		
		ArooaDescriptor descriptor = test.createDescriptor(
				getClass().getClassLoader());
		
		ArooaBeanDescriptor beanDescriptor =
			descriptor.getBeanDescriptor(
					new SimpleArooaClass(OurBean.class),
					new BeanUtilsPropertyAccessor());
		
		assertNotNull(beanDescriptor);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				beanDescriptor.getConfiguredHow("fruit"));
		
		assertEquals("stuff", beanDescriptor.getComponentProperty());		
	}
	
	public static class OurBean2Interceptor implements ParsingInterceptor {
		@Override
		public ArooaContext intercept(ArooaContext suggestedContext)
				throws ArooaConfigurationException {
			return null;
		}
	}
	
	@ArooaInterceptor("org.oddjob.arooa.deploy.ArooaDescriptorBeanMoreTest$OurBean2Interceptor")
	public static class OurBean2 {
		
		@ArooaHidden
		public void setFruit(String fruit) {
			
		}
		
		@ArooaElement
		public void setColour(String colour) {
			
		}
		
		@ArooaComponent
		public void setComponent(Object component) {
			
		}
		
		@ArooaText
		public void setText(String text) {
			
		}
	}
	
	/** Check both get applied but XML Descriptor wins. */
	public void testBeanDescriptorWithAnnotaitons() {
		
		String xml =
				"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			    " <values>" +
			    "  <arooa:bean-def element='my-bean'" +
			    "      className='org.oddjob.arooa.deploy.ArooaDescriptorBeanMoreTest$OurBean2'>" +
			    "   <properties>" +
			    "     <arooa:property name='colour' type='HIDDEN' />" +
			    "   </properties>" +
                "  </arooa:bean-def>" +
                " </values>" +
                "</arooa:descriptor>";
		
		ArooaDescriptor descriptor = 
				new ConfigurationDescriptorFactory(
						new XMLConfiguration(
								"XML", xml)).createDescriptor(
								getClass().getClassLoader());
		
		ArooaBeanDescriptor result = 
				descriptor.getBeanDescriptor(
						new SimpleArooaClass(OurBean2.class), 
						new BeanUtilsPropertyAccessor());
		
		assertEquals(ConfiguredHow.HIDDEN, 
				result.getConfiguredHow("fruit"));
		assertEquals(ConfiguredHow.HIDDEN, 
				result.getConfiguredHow("colour"));
		assertEquals("component", 
				result.getComponentProperty());
		assertEquals("text", 
				result.getTextProperty());
		
		assertEquals(OurBean2Interceptor.class, 
				result.getParsingInterceptor().getClass());
	}
}
