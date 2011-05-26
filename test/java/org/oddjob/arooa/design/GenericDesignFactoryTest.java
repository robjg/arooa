package org.oddjob.arooa.design;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;

public class GenericDesignFactoryTest extends TestCase {

	
	public static class Primatives {
		
		public void setBoolean(boolean value) {};
		public void setByte(byte value) {};
		public void setChar(char value) {};
		public void setShort(short value) {};
		public void setInteger(int value) {};
		public void setLong(long value) {};
		public void setFloat(float value) {};
		public void setDouble(double value) {};
		
	}
	
	DesignInstance design;
	
	public void testPrimatives() {
		
		GenericDesignFactory test = new GenericDesignFactory(
				new SimpleArooaClass(Primatives.class));

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
		test.testPrimatives();
		
		ViewMainHelper helper = new ViewMainHelper(test.design);
		
		helper.run();
	}
}
