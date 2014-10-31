package org.oddjob.arooa.types;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.Configured;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @oddjob.description A map allows a map of strings to values to be created.
 * <p>
 * This map will be converted to a map of string to objects during configuration
 * of a job.
 * <p>
 * As yet there is no merging of maps supported by this type.
 * 
 * @oddjob.example
 * 
 * A simple map with element access.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/MapElementTest.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/MapElementTest.txt}
 * 
 * @oddjob.example
 * 
 * Adding additional elements to a map. Also demonstrates iterable access
 * to the map.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/MapTypeAddWithSet.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/MapTypeAddWithSet.txt}
 * 
 * 
 * @author Rob Gordon.
 */
public class MapType implements ArooaValue, Serializable {
	private static final long serialVersionUID = 20140227;
		
	public static final ArooaElement ELEMENT = new ArooaElement("map");
		
    /**
     * @oddjob.property values
     * @oddjob.description Any values.
     * @oddjob.required No.
     */
	private final Map<String, ArooaValue> values = 
		new LinkedHashMap<String, ArooaValue>();
	        
	/** Values added after configuration. */
	private final Map<String, ArooaValue> extras = 
		new LinkedHashMap<String, ArooaValue>();
	
    /**
     * @oddjob.property
     * @oddjob.description The required element type. If this is specified
     * all elements of the array will attempt to be converted to this type.
     * @oddjob.required No. Elements will be left being what they want to
     * be.
     */
	private volatile Class<?> elementType;
	
	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(MapType.class, 
					new Joker<MapType>() {
				public <T> ConversionStep<MapType, T> lastStep(
						Class<? extends MapType> from, 
						final Class<T> to, 
						ConversionLookup conversions) {

					@SuppressWarnings("rawtypes")
					final ConversionPath<Map, T> finalConversion = 
							conversions.findConversion(Map.class, to);
					
			    	if (finalConversion == null) {
				    	return null;
			    	}
			    	
					return new ConversionStep<MapType, T>() {
						public Class<MapType> getFromClass() {
							return MapType.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						public T convert(MapType from, ArooaConverter converter) 
						throws ArooaConversionException {
								
							Map<String, ?> map = from.convertContents(
									converter, Object.class);
									
							return finalConversion.convert(map, converter);
						}
					};
		    	}
			});
		}
	}
		
	@Configured
	public synchronized void configured() {
		extras.clear();
	}
	
	public void setElementType(Class<?> elementType)  {
		this.elementType = elementType;
	}
	
	public Class<?> getElementType() {
		return elementType;
	}
	
	/*
	 * Set the values.
	 */
	public synchronized void setValues(String key, ArooaValue element) {
		if (element == null) {
			values.remove(key);
		}
		else {
			values.put(key, element);
		}
	}

	public synchronized ArooaValue getValues(String key) {
		return values.get(key);
	}
	
    /**
     * Convert/merge the elements.
     * 
     * @param converter The ArooaConverter used to convert the
     * 			internal types.
     * @param required The required array class 
     * @return
     * 
     * @throws NoConversionAvailableException
     */
	@SuppressWarnings("unchecked")
	<T> Map<String, T> convertContents(ArooaConverter converter, Class<T> required) 
    throws ArooaConversionException {
		
    	Map<String, T> results= new LinkedHashMap<String, T>();
    	
		if (this.elementType != null) {
			
			if (
				!required.isAssignableFrom(
						this.elementType)) {
				throw new ArooaConversionException(
					"MapType can't convert to required Map of " +
					required.getComponentType() + 
					" because elementType attribute is specified as " +
					this.elementType);
			}
			required = (Class<T>) elementType;
		}
		
		Map<String, ArooaValue> valuesAndExtras = new LinkedHashMap<String, ArooaValue>();
		synchronized (this) {
			valuesAndExtras.putAll(values);
			valuesAndExtras.putAll(extras);
		}
		
    	for (Map.Entry<String, ArooaValue> entry: valuesAndExtras.entrySet()) {
    		
    		T content = converter.convert(
    				entry.getValue(), required);

    		if (content != null) {
    	    	results.put(entry.getKey(), content);
    		}
    	}

    	return Collections.synchronizedMap(results);
    }
	
	@ArooaHidden
	public synchronized void setAdd(String key, ArooaValue value) {
		this.extras.put(key, value);
	}
		
	public synchronized ArooaValue getElement(String key) {
		ArooaValue value = values.get(key);
		if (value == null) {
			value = extras.get(key);
		}
		return value;
	}
	
	@Override
    public synchronized String toString() {
    	return "Map of " + (values.size() + extras.size()) + " things.";
    }    
}
