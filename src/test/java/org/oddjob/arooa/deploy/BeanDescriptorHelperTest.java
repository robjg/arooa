package org.oddjob.arooa.deploy;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;

public class BeanDescriptorHelperTest extends Assert {

   @Test
	public void testElement() {
		
		BeanDescriptorHelper helper = new BeanDescriptorHelper(null);
		
		assertEquals(ConfiguredHow.ELEMENT, helper.getConfiguredHow("apples"));
		
		assertTrue(helper.isElement("apples"));
	}
	
	class OurBeanDescriptor extends MockArooaBeanDescriptor {
		
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
}
