/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.*;

/**
 * Provides conversions for booleans.
 * <p>
 * 
 * @author rob
 *
 */
public class BooleanConvertlets implements ConversionProvider {

    /**
     * @oddjob.conversion 0 is false, anything else is true.
     */
    static class NumberToBoolean implements Convertlet<Number, Boolean> {
        @Override
        public Boolean convert(Number from) throws ArooaConversionException {
            return !(from.intValue() == 0);
        }
    }

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Number.class, Boolean.class, new NumberToBoolean());

		registry.register(Boolean.class, Number.class,
                from -> from ?
                        Integer.valueOf(1) : Integer.valueOf(0));
		
		registry.register(String.class, Boolean.class,
                from -> {
                    String stringValue = from.trim();
                    if (stringValue.isEmpty()) {
                        return null;
                    }
                    stringValue = stringValue.toLowerCase();
                    if (stringValue.equalsIgnoreCase("yes")
                            || stringValue.equalsIgnoreCase("y")
                            || stringValue.equalsIgnoreCase("true")
                            || stringValue.equalsIgnoreCase("on")
                            || stringValue.equalsIgnoreCase("1")) {
                        return (Boolean.TRUE);
                    }
                    else if (stringValue.equalsIgnoreCase("no")
                            || stringValue.equalsIgnoreCase("n")
                            || stringValue.equalsIgnoreCase("false")
                            || stringValue.equalsIgnoreCase("off")
                            || stringValue.equalsIgnoreCase("0")) {
                        return (Boolean.FALSE);
                    }
                    else {
                        throw new ClassCastException("Can't convert [" + from
                                + "] to a boolean.");
                    }
                });
		registry.register(Boolean.class, String.class,
                (FinalConvertlet<Boolean, String>) Object::toString);
		
	}
	
}
