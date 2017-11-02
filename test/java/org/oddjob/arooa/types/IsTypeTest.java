package org.oddjob.arooa.types;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;


public class IsTypeTest extends Assert {

	// #fruitBean {	
	public static class FruitBean {
		
		private String type;
		private String colour;
				
		public void setType(String type) {
			this.type = type;
		}
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	// } #fruitBean 
	
	// #simpleBean {	
	public static class SnackBean {
		FruitBean fruit;
		
		public void setFruit(FruitBean bean) {
			this.fruit = bean;
		}
	}
	// } #simpleBean 
	
	
   @Test
	public void testSimpleProperty() throws ArooaParseException {

		SnackBean root = new SnackBean();
		
		StandardArooaParser parser = new StandardArooaParser(
				root);
		
		parser.parse(new XMLConfiguration(
				"org/oddjob/arooa/types/IsSimple.xml", 
				getClass().getClassLoader()));
		
		parser.getSession().getComponentPool().configure(root);
		
		assertEquals("red", root.fruit.colour);
		assertEquals("apple", root.fruit.type);
	}
	
	// #mappedBean {	
	public static class MappedSnack {

		private Map<String, FruitBean> fruit =
			new HashMap<String, FruitBean>();
		
		public void setFruit(String key, FruitBean bean) {
			if (fruit == null) {
				fruit.remove(key);
			}
			else {
				this.fruit.put(key, bean);
			}
		}
	}
	// } #mappedBean
	
   @Test
	public void testMappedProperty() throws ArooaParseException {
		
		MappedSnack root = new MappedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(new XMLConfiguration(
				"org/oddjob/arooa/types/IsMapped.xml", 
				getClass().getClassLoader()));
		
		parser.getSession().getComponentPool().configure(root);
				
		assertEquals("red", root.fruit.get("morning").colour);
		assertEquals("apple", root.fruit.get("morning").type);
		assertEquals("white", root.fruit.get("afternoon").colour);
		assertEquals("grapes", root.fruit.get("afternoon").type);
	}
	
	// #indexedBean {	
	public static class IndexedSnack {
		
		private List<FruitBean> fruit = 
			new ArrayList<FruitBean>();
		
		public void setFruit(int index, FruitBean bean) {
			if (bean == null) {
				fruit.remove(index);
			}
			else {
				fruit.add(index, bean);
			}
		}
	}
	// } #indexedBean
	
   @Test
	public void testIndexedProperty() throws ArooaParseException {

		IndexedSnack root = new IndexedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(new XMLConfiguration(
				"org/oddjob/arooa/types/IsIndexed.xml", 
				getClass().getClassLoader()));
		
		parser.getSession().getComponentPool().configure(root);
		
		assertEquals("red", root.fruit.get(0).colour);
		assertEquals("apple", root.fruit.get(0).type);
		assertEquals("green", root.fruit.get(1).colour);
		assertEquals("pear", root.fruit.get(1).type);
	}
	
	private class MemberClass {
		
	}
	
	private static class PackageClass {
		
	}
	
	public interface Interface {
		
	}
	
	public static class NoNoArgConstrcutor {
		public NoNoArgConstrcutor(String ignored) {
		}
	}
	
   @Test
	public void testSupports() {
	
		assertFalse(IsType.supports(
				new InstantiationContext(ArooaType.VALUE, 
						null)));
		
		assertTrue(IsType.supports(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(FruitBean.class))));
		
		assertFalse(IsType.supports(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(MemberClass.class))));
		
		assertFalse(IsType.supports(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(PackageClass.class))));
		
		assertFalse(IsType.supports(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(Interface.class))));
		
		assertFalse(IsType.supports(
				new InstantiationContext(ArooaType.VALUE, 
						new SimpleArooaClass(NoNoArgConstrcutor.class))));
	}
}
