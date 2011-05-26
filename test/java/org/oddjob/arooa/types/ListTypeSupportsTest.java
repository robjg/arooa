package org.oddjob.arooa.types;

import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.DefaultConverter;

public class ListTypeSupportsTest extends TestCase {

	public void testSupports() {
		
		ConversionLookup converter = new DefaultConverter();
		
		assertNotNull(converter.findConversion(
				ListType.class, List.class));
		
		assertNotNull(converter.findConversion(
				ListType.class, Object[].class));
		
		assertNotNull(converter.findConversion(
				ListType.class, String[].class));
		
		assertNotNull(converter.findConversion(
				ListType.class, Object.class));
		
		assertNotNull(converter.findConversion(
				ListType.class, ArooaValue.class));
	}
	
	public void testNotSupports() {
		
		ConversionLookup converter = new DefaultConverter();
		
		assertNull(converter.findConversion(
				ListType.class, String.class));
		
		assertNull(converter.findConversion(
				ListType.class, ArooaObject.class));
	}
	
}
