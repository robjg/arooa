package org.oddjob.arooa.reflect;

import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

import org.junit.Assert;

/**
 * Thinking about how to handle/if to handle bound properties.
 *  
 * @author rob
 *
 */
public class EventDescriptorTest extends Assert {
	
	public static class Bean {
		
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			
		}
		
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			
		}
	}
	
   @Test
	public void testPropertyListenerIntrospection() throws IntrospectionException {
		
		BeanInfo beanInfo = Introspector.getBeanInfo(Bean.class);
		
		EventSetDescriptor[] descriptors = beanInfo.getEventSetDescriptors();
		
		assertEquals(1, descriptors.length);
	}

	class MyListener implements EventListener {
		
	}
	
	public static class Bean2 {
	
		public void addMyListener(MyListener listener) {
			
		}
		
		public void removeMyListener(MyListener listener) {
			
		}
	}
	
   @Test
	public void testMyListenerIntrospection() throws IntrospectionException {
		
		BeanInfo beanInfo = Introspector.getBeanInfo(Bean2.class);
		
		EventSetDescriptor[] descriptors = beanInfo.getEventSetDescriptors();
		
		assertEquals(1, descriptors.length);
		
		assertEquals("my", descriptors[0].getName());
		assertEquals(MyListener.class, descriptors[0].getListenerType());
	}
}
