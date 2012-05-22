package org.oddjob.arooa.deploy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaDescriptor;
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
	
}
