/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.FinalConvertlet;

/**
 * Provides conversions for booleans.
 * <p>
 * 
 * @author rob
 *
 */
public class BooleanConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Number.class, Boolean.class, 
				new Convertlet<Number, Boolean>() {
			public Boolean convert(Number from) {
				return new Boolean(! (from.intValue() == 0));
			};
		});
		
		registry.register(Boolean.class, Number.class, 
				new Convertlet<Boolean, Number>() {
			public Number convert(Boolean from) {
				return from.booleanValue() ? 
						new Integer(1) : new Integer(0);
			}
		});
		
		registry.register(String.class, Boolean.class, 
				new Convertlet<String, Boolean>() {
			public Boolean convert(String from) {
				String stringValue = from.toLowerCase();
				if (stringValue.equalsIgnoreCase("yes")
						|| stringValue.equalsIgnoreCase("y")
						|| stringValue.equalsIgnoreCase("true")
						|| stringValue.equalsIgnoreCase("on")
						|| stringValue.equalsIgnoreCase("1")) {
					return (Boolean.TRUE);
				} else if (stringValue.equalsIgnoreCase("no")
						|| stringValue.equalsIgnoreCase("n")
						|| stringValue.equalsIgnoreCase("false")
						|| stringValue.equalsIgnoreCase("off")
						|| stringValue.equalsIgnoreCase("0")) {
					return (Boolean.FALSE);
				} else {
					throw new ClassCastException("Can't convert [" + from
							+ "] to a boolean.");
				}
			}
		});
		registry.register(Boolean.class, String.class, 
				new FinalConvertlet<Boolean, String>() {
			public String convert(Boolean from) {
				return from.toString();
			}
		});
		
	}
	
}
