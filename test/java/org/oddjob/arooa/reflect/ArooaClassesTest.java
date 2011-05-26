package org.oddjob.arooa.reflect;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;

import junit.framework.TestCase;

public class ArooaClassesTest extends TestCase {

	private class Stuff {
		
	}
	
	static private class OurArooaClass extends MockArooaClass {
		
	}
	
	static {
		ArooaClasses.register(Stuff.class, 
				new ArooaClassFactory<Stuff>() {
			
			@Override
			public ArooaClass classFor(Stuff instance) {
				
				return new OurArooaClass();
			}
		});		
	}
	
	public void testFindArooaClass() {
		
		ArooaClass result = ArooaClasses.classFor(new Stuff());
		
		assertEquals(OurArooaClass.class, result.getClass());
		
	}
	
	public void testStandardFactories() throws ClassNotFoundException {

		Class.forName(BeanUtilsPropertyAccessor.class.getName());
		
		String simple = new String("simple");
		
		ArooaClass result = ArooaClasses.classFor(simple);
		
		assertEquals(SimpleArooaClass.class, result.getClass());
	}
}
