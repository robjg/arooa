package org.oddjob.arooa.deploy;

import org.junit.Test;

import java.lang.reflect.Method;

import org.junit.Assert;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.Configured;
import org.oddjob.arooa.life.Destroy;
import org.oddjob.arooa.life.Initialised;
import org.oddjob.arooa.life.SimpleArooaClass;

public class ArooaAnnotationsHelperTest extends Assert {

	public static class MyBase {
		
		@ArooaAttribute
		private String shape;

		@Destroy
		public void myDestroy() { }
		
		public void myInit() { }
	}
	
	public static class MyBean extends MyBase {
		
		@ArooaHidden
		private String colour;
		
		@ArooaComponent
		public void setFruit() { }
		
		@Configured
		public void doStuff() { }
	}
	
	
   @Test
	public void testMethodAnnotations() {
		
		ArooaAnnotationsHelper test = new ArooaAnnotationsHelper(
				new SimpleArooaClass(MyBean.class));
	
		Method method = test.methodFor(ArooaComponent.class.getName());
		
		assertEquals("setFruit", method.getName());

		method = test.methodFor(Configured.class.getName());
		
		assertEquals("doStuff", method.getName());

		AnnotationDefinition def = new AnnotationDefinition();
		def.setMethod("missing");
		def.setName("org.oddjob.test.Anything");

		try {
			test.addAnnotationDefintion(def);
		}
		catch (RuntimeException e) {
			assertEquals(NoSuchMethodException.class, 
					e.getCause().getClass());
		}
		
		def.setMethod("doStuff");
		
		test.addAnnotationDefintion(def);
		
		method = test.methodFor("org.oddjob.test.Anything");
		
		assertEquals("doStuff", method.getName());
		
		method = test.methodFor(Destroy.class.getName());

		assertEquals("myDestroy", method.getName());
		
		def.setMethod("myInit");
		def.setName(Initialised.class.getName());
		
		test.addAnnotationDefintion(def);
		
		method = test.methodFor(Initialised.class.getName());

		assertEquals("myInit", method.getName());
	}
	
   @Test
	public void testPropertyAnnotations() {
		
		ArooaAnnotationsHelper test = new ArooaAnnotationsHelper(
				new SimpleArooaClass(MyBean.class));

		String[] properties = test.annotatedProperties();
		
		assertEquals(3, properties.length);
		
		ArooaAnnotation[] annotations = test.annotationsForProperty("colour");
		
		assertEquals(1, annotations.length);
		
		ArooaAnnotation annotation = test.annotationForProperty("colour",
				ArooaHidden.class.getName());
		
		assertEquals(ArooaHidden.class, 
				annotation.realAnnotation(
						ArooaHidden.class).annotationType());
		
		annotation = test.annotationForProperty("fruit",
				ArooaComponent.class.getName());
		
		assertEquals(ArooaComponent.class, 
				annotation.realAnnotation(
						ArooaComponent.class).annotationType());
		
		assertNull(test.annotationForProperty("colour", 
				"org.oddjob.test.Anything"));
		
		PropertyDefinition definition = new PropertyDefinition();
		definition.setName("colour");
		definition.setAnnotation("org.oddjob.test.Anything");
		
		test.addPropertyDefinition(definition);
		
		annotation = test.annotationForProperty("colour", 
				"org.oddjob.test.Anything");
				
		assertEquals("org.oddjob.test.Anything", annotation.getName());
		
		annotation = test.annotationForProperty("shape", 
				ArooaAttribute.class.getName());
				
		assertEquals(ArooaAttribute.class.getName(), annotation.getName());
	}
	
	
}
