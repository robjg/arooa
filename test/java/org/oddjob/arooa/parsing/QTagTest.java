package org.oddjob.arooa.parsing;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class QTagTest extends TestCase {

	public void testQName() {
		// what's wrong with QName?
		
		QName test = new QName("http://fruit.foo", "apple", "fruit");
		
		
		// this is to string - and there's no method for creating or parsing
		// fruit:apple - which is why we had to create 
		assertEquals("{http://fruit.foo}apple", test.toString());
		
		
	}
	
	public void testToString() throws URISyntaxException {
		
		QTag test1 = new QTag("apple");
		
		assertEquals("apple", test1.toString());

		QTag test2 = new QTag("fruit", 
				new ArooaElement(new URI("http://fruit"), "apple"));
		
		assertEquals("fruit:apple", test2.toString());
	}
	
	public void testComparable() throws URISyntaxException {
		
		assertTrue(new QTag("orange").compareTo(new QTag("apple")) > 0);
		assertTrue(new QTag("apple").compareTo(new QTag("orange")) < 0);
		
		assertTrue(new QTag("fruit", new ArooaElement(new URI("http://fruit"), "apple")).compareTo(new QTag("orange")) > 0);
	}
}
