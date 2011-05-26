package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;

public class BeanDescriptorHelperTest extends TestCase {

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
	
	public void testElement2() {
		
		BeanDescriptorHelper helper = new BeanDescriptorHelper(
				new OurBeanDescriptor());
		
		assertEquals(ConfiguredHow.ELEMENT, helper.getConfiguredHow("apples"));
		
		assertTrue(helper.isElement("apples"));
	}
}
