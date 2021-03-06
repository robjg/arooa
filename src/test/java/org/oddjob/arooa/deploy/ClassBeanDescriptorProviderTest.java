package org.oddjob.arooa.deploy;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;

public class ClassBeanDescriptorProviderTest extends Assert {

	private class Thing {};
	
	public static class ThingArooa extends MockArooaBeanDescriptor {

		public ParsingInterceptor getParsingInterceptor() {
			return null;
		}
		
		public String getTextProperty() {
			return null;
		}
		
		public String getComponentProperty() {
			return null;
		}
		
		public boolean isAtttributeProperty(String property) {
			return false;
		}
	}
		
   @Test
	public void testClassBeanDescriptor() {
		
		ClassBeanDescriptorProvider test = new ClassBeanDescriptorProvider();
		
		ArooaBeanDescriptor descriptor = test.getBeanDescriptor(
				new SimpleArooaClass(Thing.class), 
				new BeanUtilsPropertyAccessor());
		
		assertNotNull(descriptor);
	}
}
