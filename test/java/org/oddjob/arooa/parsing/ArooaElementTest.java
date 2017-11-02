package org.oddjob.arooa.parsing;

import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;

import org.oddjob.ArooaTestHelper;

public class ArooaElementTest extends Assert {

   @Test
	public void testEquals1() {
		
		ArooaElement a1 = new ArooaElement("a");
		ArooaElement a2 = new ArooaElement("a");
		
		assertEquals(a1, a2);
		
		assertEquals(a1.hashCode(), a2.hashCode());
	}
	
   @Test
	public void testEquals2() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement(
				new URI("urn:test:test"), "a");
		ArooaElement a2 = new ArooaElement(
				new URI("urn:test:test"),
				"a");
		
		assertEquals(a1, a2);
		
		assertEquals(a1.hashCode(), a2.hashCode());
	}
	
   @Test
	public void testEquals3() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement(
				new URI("urn:test:test"), "a");
		ArooaElement a2 = new ArooaElement(
				new URI("urn:test:test2"),
				"a");
		
		assertNotSame(a1, a2);
	}
	
   @Test
	public void testEquals4() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement(
				new URI("urn:test:test"), "a");
		ArooaElement a2 = new ArooaElement(
				"a");
		
		assertNotSame(a1, a2);
	}
	
   @Test
	public void testEquals5() throws URISyntaxException {
		
		ArooaElement a1 = new ArooaElement("a");
		ArooaElement a2 = new ArooaElement("b");
		
		assertNotSame(a1, a2);
	}
	
   @Test
	public void testSerialize() throws URISyntaxException, IOException, ClassNotFoundException {
		
		ArooaElement test = new ArooaElement(
				new URL("http://rgordon.co.uk/oddjob/test").toURI(), "foo");
		
		ArooaElement copy = ArooaTestHelper.copy(test);
		
		assertEquals(test, copy);
	}
	
   @Test
	public void testToString() throws MalformedURLException, URISyntaxException {
		
		ArooaElement test = new ArooaElement(
				new URL("http://rgordon.co.uk/oddjob/test").toURI(), "foo");

		assertEquals("http://rgordon.co.uk/oddjob/test:foo", test.toString());
		
	}
}
