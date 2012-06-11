package org.oddjob.arooa.deploy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ArooaDescriptorAnnotationsTest extends TestCase {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface DoStuff {

	}
	
	public static class MyBean {
		
		@DoStuff
		public void myDoStuff() {
			
		}
		
		public void myDestroy() {
			
		}
		
		public void setColour(String colour) {
			
		}
	}
	
	public void testDescriptorAndAnnotations() {
		
		ArooaDescriptor descriptor = 
				new ConfigurationDescriptorFactory(
						new XMLConfiguration(
								"org/oddjob/arooa/deploy/ArooaDescriptorAnnotationsTest.xml",
								getClass().getClassLoader())).createDescriptor(
								getClass().getClassLoader());

		BeanUtilsPropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		ArooaAnnotations test = descriptor.getBeanDescriptor(
				new SimpleArooaClass(MyBean.class), 
				accessor).getAnnotations();
		
		Method doStuff = test.methodFor(DoStuff.class.getName());
		assertEquals("myDoStuff", doStuff.getName());
		
		Method destroy = test.methodFor("org.oddjob.arooa.life.ArooaDestroy");
		assertEquals("myDestroy", destroy.getName());
		
		assertNull(test.methodFor("idontexist"));
	}
	
	public void testPropertyAnnotations() {
		
		ArooaDescriptor descriptor = 
				new ConfigurationDescriptorFactory(
						new XMLConfiguration(
								"org/oddjob/arooa/deploy/ArooaDescriptorAnnotationsTest2.xml",
								getClass().getClassLoader())).createDescriptor(
								getClass().getClassLoader());

		BeanUtilsPropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				new SimpleArooaClass(MyBean.class), 
				accessor);
		
		ArooaAnnotations test = beanDescriptor.getAnnotations();
		
		ArooaAnnotation[] annotations = test.annotationsForProperty(
				"colour");
		
		assertEquals(2, annotations.length);
		
		List<String> results = Arrays.asList(new String[] { 
				annotations[0].getName(), annotations[1].getName()		
		});
		
		assertTrue(results.contains(
				"org.oddjob.arooa.deploy.annotations.Component"));
		assertTrue(results.contains(
				"org.oddjob.arooa.deploy.test.Special"));
		
		// test property annotation still has default type.

		assertEquals(ConfiguredHow.ATTRIBUTE, 
					beanDescriptor.getConfiguredHow("colour"));
	}
}
