package org.oddjob.arooa.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.xml.XMLConfiguration;

public class IndexedPropertyTest extends TestCase {

	public static class SimpleAndIndexed {
		
		List<String> things = new ArrayList<String>();
		
		@ArooaAttribute
		public void setThings(String[] things) {
			this.things.addAll(Arrays.asList(things));
		}
		public void setThings(int index, String thing) {
			throw new RuntimeException("Unexpected");
		}
		
		public String[] getStuff() {
			return new String[] { "apples", "oranges" };
		}
	}
	
	public void testSettingAsAttributeFails() throws ArooaParseException {

		String xml = 
			"<x id='x' things='${x.stuff}'/>";
		
		SimpleAndIndexed x = new SimpleAndIndexed();
		
		StandardArooaParser parser = new StandardArooaParser(x);
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		try {
			parser.getSession().getComponentPool().configure(x);
			fail("Is impossible to use the array setter as a simple property like this.");
		}
		catch (Exception e){
			// expected.
		}
	}
	
	public void testElementFails() {

		String xml = 
			"<x>" +
			" <things>" +
			"  <value value='apples'/>" +
			" </things>" +
			"</x>";
		
		SimpleAndIndexed x = new SimpleAndIndexed();
		
		StandardArooaParser parser = new StandardArooaParser(x);
		
		try {
			parser.parse(new XMLConfiguration("TEST", xml));
			fail("Should fail.");
		} catch (Exception e) {
			assertEquals("Property things is not configured as an element.",
					e.getMessage());
		}
		
	}
	
	public static class SimpleAndIndexed2 {
		
		List<String> things = new ArrayList<String>();
		
		public void setThings(String[] things) {
			throw new RuntimeException("This should be ignored.");
		}
		public void setThings(int index, String thing) {
			if (thing == null) {
				things.remove(index);
			}
			else {
				things.add(index, thing);
			}
		}
		
		public String[] getStuff() {
			return new String[] { "apples", "oranges" };
		}
	}
	
	/**
	 * Just check understanding of BeanUtils while were here...
	 * @throws ArooaParseException 
	 */
	public void testSettingFromArrays() throws ArooaParseException {

		String xml = 
			"<x id='x'>" +
			" <things>" +
			"  <value value='${x.stuff[0]}'/>" +
			"  <value value='${x.stuff[1]}'/>" +
			" </things>" +
			"</x>";
		
		SimpleAndIndexed2 x = new SimpleAndIndexed2();
		
		StandardArooaParser parser = new StandardArooaParser(x);
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		parser.getSession().getComponentPool().configure(x);
		
		assertEquals(2, x.things.size());
	}
	
	public void testConfigureConigureDestroy() throws ArooaParseException {
		
		String xml = 
			"<x>" +
			" <things>" +
			"  <value value='a'/>" +
			" </things>" +
			"</x>";
		
		SimpleAndIndexed2 x = new SimpleAndIndexed2();
		
		StandardArooaParser parser = new StandardArooaParser(x);
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		assertEquals(0, x.things.size());
		
		parser.getSession().getComponentPool().configure(x);
		
		assertEquals(1, x.things.size());
		
		parser.getSession().getComponentPool().configure(x);
		
		assertEquals(1, x.things.size());
			
		parser.getSession().getComponentPool().contextFor(x).getRuntime().destroy();
		
		assertEquals(0, x.things.size());
	}
}
