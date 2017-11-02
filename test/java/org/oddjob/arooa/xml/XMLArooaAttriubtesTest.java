package org.oddjob.arooa.xml;

import org.junit.Test;

import org.junit.Assert;

import org.xml.sax.helpers.AttributesImpl;

public class XMLArooaAttriubtesTest extends Assert {

   @Test
	public void testGet() {
		
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "class", "class", "java.lang.String", "a.b.C");
        atts.addAttribute("", "foo", "foo", "java.lang.String", "bar");
		
		XMLArooaAttributes test = new XMLArooaAttributes("", atts);
		
		assertEquals("a.b.C", test.get("class"));
		assertEquals("bar", test.get("foo"));		
	}
}
