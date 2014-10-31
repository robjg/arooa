/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

/**
 * Provide {@link org.oddjob.arooa.Convertlet}s between Arrays and Collections.
 * <p>
 * Note these are {@link org.oddjob.arooa.FinalConvertlet}s to stop them being 
 * used in unexpected ways.
 * 
 * @author rob
 *
 */
public class CollectionConvertlets implements ConversionProvider {

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.convert.ConvertletProvider#registerWith(org.oddjob.arooa.convert.ConvertletRegistry)
	 */
	@SuppressWarnings("rawtypes")
	public void registerWith(ConversionRegistry registry) {

		registry.register(Collection.class, Object[].class, 
				new FinalConvertlet<Collection, Object[]>() {
			public Object[] convert(Collection from) {
				return (from).toArray();
			};
		});
		
		registry.register(Object[].class, List.class, 
				new FinalConvertlet<Object[], List>() {
			public List<Object> convert(Object[] from) {
				return Arrays.asList(from);
			};
		});
		
		registry.register(Map.class, Collection.class, 
				new Convertlet<Map, Collection>() {
			@SuppressWarnings("unchecked")
			public Collection<Map.Entry> convert(Map from) {
				return new ArrayList<Map.Entry>(from.entrySet());
			};
		});
	}	
}
