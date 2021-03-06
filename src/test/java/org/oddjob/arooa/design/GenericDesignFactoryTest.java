package org.oddjob.arooa.design;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;

public class GenericDesignFactoryTest extends Assert {

	
	public static class Primitives {
		
		public void setBoolean(boolean value) {}

		public void setByte(byte value) {}

		public void setChar(char value) {}

		public void setShort(short value) {}

		public void setInteger(int value) {}

		public void setLong(long value) {}

		public void setFloat(float value) {}

		public void setDouble(double value) {}

	}
	
	DesignInstance design;
	
   @Test
	public void testPrimitives() {
		
		GenericDesignFactory test = new GenericDesignFactory(
				new SimpleArooaClass(Primitives.class));

		DesignSeedContext context = new DesignSeedContext(
				ArooaType.VALUE, new StandardArooaSession());
		
		DesignInstanceBase design = 
			(DesignInstanceBase) test.createDesign(
					new ArooaElement("test"), 
					context);

		DesignProperty[] properties = design.children();
		
		assertEquals(8, properties.length);
		
		for (DesignProperty prop: properties) {
			assertTrue(prop instanceof DesignAttributeProperty);
		}
		
		
		this.design = design;
	
	}
	
	public static class Fruit {
		
		String name;

		Fruit more;
		
		public Fruit getMore() {
			return more;
		}

		public void setMore(Fruit more) {
			this.more = more;
		}

		public String getName() {
			return name;
		}

		public void setName(String id) {
			this.name = id;
		}
	}
	
   @Test
	public void testThingWithAnElement() {
		
		GenericDesignFactory factory = new GenericDesignFactory(
				new SimpleArooaClass(Fruit.class));

	   this.design = factory.createDesign(
			   new ArooaElement("foo"),
			   new DesignSeedContext(ArooaType.VALUE, new StandardArooaSession()));
	}
	
//	public void testMagicBean() {
//		
//		MagicBeanDef def = new MagicBeanDef();
//
//		MagicBeanProperty prop1 = new MagicBeanProperty();
//		prop1.setName("fruit");
//		prop1.setType(String.class.getName());
//		
//		MagicBeanProperty prop2 = new MagicBeanProperty();
//		prop2.setName("quantity");
//		prop2.setType(Integer.class.getName());
//		
//		def.setProperties(0, prop1);
//		def.setProperties(1, prop2);
//		
//		ArooaClass classIdentifier = def.createMagic(
//				this.getClass().getClassLoader());
//		
//		GenericDesignFactory test = new GenericDesignFactory(
//				classIdentifier);
//
//		DesignSeedContext context = new DesignSeedContext(
//				ArooaType.VALUE, new StandardArooaSession());
//		
//		DesignInstanceBase design = 
//			(DesignInstanceBase) test.createDesign(
//					new ArooaElement("test"), 
//					context);
//
//		DesignProperty[] properties = design.children();
//		
//		assertEquals(8, properties.length);
//		
//		for (DesignProperty prop: properties) {
//			assertTrue(prop instanceof DesignAttributeProperty);
//		}
//		
//		
//		this.design = design;
//	
//	}
	
	
	public static void main(String... args) {
		
		GenericDesignFactoryTest test = new GenericDesignFactoryTest();
		test.testThingWithAnElement();
		
		ViewMainHelper helper = new ViewMainHelper(test.design);
		
		helper.run();
	}
}
