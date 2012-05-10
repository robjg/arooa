package org.oddjob.arooa.design;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;

public class DesignPropertyBaseTest extends TestCase {

	public static interface Fruit {
		
	}
	
	public static class Snack {
		
		public void setFruit(Fruit fruit) {}
	}
	
	
	class OurDesignProperty extends DesignPropertyBase {
		
		public OurDesignProperty(String property, DesignInstance owner) {
			super(property, owner);
		}

		@Override
		void insertInstance(int index, DesignInstance instance) {
			throw new RuntimeException("Unexpected.");
		}

		public DesignInstance instanceAt(int index) {
			throw new RuntimeException("Unexpected.");
		}

		public int instanceCount() {
			throw new RuntimeException("Unexpected.");
		}

		public void clear() {
			throw new RuntimeException("Unexpected.");
		}

		public boolean isPopulated() {
			throw new RuntimeException("Unexpected.");
		}

		public FormItem view() {
			throw new RuntimeException("Unexpected.");
		}
		
		
	}

	class OurDesign extends DesignValueBase {

		OurDesignProperty fruit;
		
		public OurDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Snack.class), parentContext);
			
			fruit = new OurDesignProperty("fruit", this);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { fruit };
		}
		
		public Form detail() {
			throw new RuntimeException("Unexpected.");
		}
		
	}
	
	public void testConstructor() {
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.VALUE, new StandardArooaSession());
		
		OurDesign design = new OurDesign(
				new ArooaElement("snack"), context);
		
		assertEquals(new SimpleArooaClass(Fruit.class),
				design.fruit.getArooaContext().getRuntime(
						).getClassIdentifier());
		
		assertEquals(ArooaType.VALUE,
				design.fruit.getArooaContext().getArooaType());
	}
}
