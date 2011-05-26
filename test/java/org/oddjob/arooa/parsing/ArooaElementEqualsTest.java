package org.oddjob.arooa.parsing;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

public class ArooaElementEqualsTest extends TestCase {

	public void testEquals1() {
		
		ArooaElement a1 = new ArooaElement("a");
		ArooaElement a2 = new ArooaElement("a");
		
		assertEquals(a1, a2);
		
		assertEquals(a1.hashCode(), a2.hashCode());
	}
	
	public void testEquals2() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement(
				new URI("urn:test:test"), "a");
		ArooaElement a2 = new ArooaElement(
				new URI("urn:test:test"),
				"a");
		
		assertEquals(a1, a2);
		
		assertEquals(a1.hashCode(), a2.hashCode());
	}
	
	public void testEquals3() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement(
				new URI("urn:test:test"), "a");
		ArooaElement a2 = new ArooaElement(
				new URI("urn:test:test2"),
				"a");
		
		assertNotSame(a1, a2);
	}
	
	public void testEquals4() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement(
				new URI("urn:test:test"), "a");
		ArooaElement a2 = new ArooaElement(
				"a");
		
		assertNotSame(a1, a2);
	}
	
	public void testEquals5() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement("a");
		ArooaElement a2 = new ArooaElement("b");
		
		assertNotSame(a1, a2);
	}
	

}
