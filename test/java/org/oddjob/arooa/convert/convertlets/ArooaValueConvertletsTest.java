/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.convertlets.CheckingConvertletRegistry.Check;

public class ArooaValueConvertletsTest extends TestCase {
	
	public void testAll() {
		CheckingConvertletRegistry checking = new CheckingConvertletRegistry(
				new Check[] { new Check() {
					public <F, T> void check(Class<F> from, Class<T> to,
							Convertlet<F, T> convertlet) throws Exception {
						assertEquals(Object.class, from);
						assertEquals(ArooaValue.class, to);
						ArooaValue result = (ArooaValue) convertlet
								.convert(from.cast("Test"));
						
						DefaultConverter converter = new DefaultConverter();
						
						assertEquals("Test", 
								converter.convert(result, String.class));
					};
				}, });

		new ArooaValueConvertlets().registerWith(checking);
		
		assertEquals(1, checking.count);
	}
	
}
