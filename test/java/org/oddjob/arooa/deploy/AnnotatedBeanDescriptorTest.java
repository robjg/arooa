package org.oddjob.arooa.deploy;

import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.deploy.annotations.ArooaElement;
import org.oddjob.arooa.deploy.annotations.ArooaInterceptor;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;

public class AnnotatedBeanDescriptorTest extends Assert {

	public static class OurInterceptor implements ParsingInterceptor {
		public ArooaContext intercept(ArooaContext suggestedContext) {
			return null;
		}
	}
	
	@ArooaInterceptor("org.oddjob.arooa.deploy.AnnotatedBeanDescriptorTest$OurInterceptor")
	public static class Bean {
		
		@ArooaComponent
		public void setMyComp(Object ignored) {}
		
		@ArooaText
		public void setMyText(Object ignored) {}

		@Inject @Named("red")
		@ArooaAttribute
		public void setMyAttribute(Object ignored) {}

		@Inject
		@ArooaElement
		public void setMyValue(Object ignored) {}

	}
	
   @Test
	public void testDescriptor() {
		
		AnnotatedBeanDescriptorProvider test= 
			new AnnotatedBeanDescriptorProvider();
		
		ArooaBeanDescriptor beanDescriptor = 
			test.getBeanDescriptor(
					new SimpleArooaClass(Bean.class),
					new BeanUtilsPropertyAccessor());
		
		assertNotNull(beanDescriptor);
		
		ParsingInterceptor interceptor = 
				beanDescriptor.getParsingInterceptor();
		
		assertEquals(OurInterceptor.class, interceptor.getClass());
		
		assertEquals("myText", beanDescriptor.getTextProperty());
		
		assertEquals("myComp", beanDescriptor.getComponentProperty());
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				beanDescriptor.getConfiguredHow("myAttribute"));
		
		assertEquals(ConfiguredHow.ELEMENT, 
				beanDescriptor.getConfiguredHow("myValue"));
		
		assertEquals(true, beanDescriptor.isAuto("myAttribute"));
		
		assertEquals(true, beanDescriptor.isAuto("myValue"));
		
		assertEquals("red", beanDescriptor.getFlavour("myAttribute"));
		
		assertEquals(null, beanDescriptor.getFlavour("myValue"));
	}
	
}
