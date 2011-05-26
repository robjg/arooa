package org.oddjob.arooa.deploy;

import java.util.Date;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;

public class DefaultBeanDescriptorProviderTest extends TestCase {

	enum Type {
		COX,
		GRANNY_SMITH,
		PINK_LADY;
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
	
	
	public void testIsAttribute() {
		
		DefaultBeanDescriptorProvider test = 
			new DefaultBeanDescriptorProvider();
		
			
		ArooaBeanDescriptor beanDescriptor = test.getBeanDescriptor(
				new SimpleArooaClass(Apple.class),
				new BeanUtilsPropertyAccessor());

		BeanDescriptorHelper sort = new BeanDescriptorHelper(beanDescriptor);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				sort.getConfiguredHow("colour"));
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				sort.getConfiguredHow("quantity"));
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				sort.getConfiguredHow("type"));

		assertEquals(ConfiguredHow.ELEMENT, 
				sort.getConfiguredHow("date"));
	}
			
}
