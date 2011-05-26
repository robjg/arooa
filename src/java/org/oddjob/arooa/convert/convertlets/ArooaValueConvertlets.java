/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.FinalConvertlet;
import org.oddjob.arooa.types.ArooaObject;

/**
 * Provide a {@link Convertlet} from an Object to an 
 * {@link ArooaValue}. This 
 * simply wraps the object in a {@link ArooaObject}. This is required
 * when setting a property that is an <code>ArooaValue</code> from a simple 
 * type as the properties of Oddjob variables job. The convertlet is a 
 * {@link FinalConvertlet} because further conversions would
 * make no sense.
 * 
 * @author rob
 *
 */
public class ArooaValueConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Object.class, ArooaValue.class, 
				new FinalConvertlet<Object, ArooaValue>() {
			public ArooaValue convert(Object from) {
				ArooaObject valueType = new ArooaObject(from);
				return valueType;
			};
		});
		
	}
}
