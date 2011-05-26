/*
  Copyrih bhzeerf(c) 2005, Rob Gordon.
 */
package org.oddjob.arooa.types;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.utils.ListSetterHelper;

/**
 * @oddjob.description A list provides a way of setting properties that are
 * either {@link java.util.List} types or arrays. A list can include any other 
 * type including another list or array type.
 * <p>
 * Handling of multi-dimensional arrays has not been considered. Such properties
 * are probably best defined with a custom {@link org.oddjob.arooa.ArooaValue}.
 * 
 * @oddjob.example
 * 
 * A simple list of things.
 * 
 * <pre>
 * &lt;list id="list"&gt;
 *  &lt;values&gt;
 *   &lt;value value="Hello World"/&gt;
 *   &lt;value value="Goodbye World"/&gt;
 *   &lt;list&gt;
 *    &lt;values&gt;
 *     &lt;value value="I'm in another list"/&gt;
 *    &lt;/values&gt;
 *   &lt;/list&gt;
 *  &lt;/values&gt;
 * &lt;/list&gt;
 * </pre>
 * 
 * @oddjob.example
 * 
 * A Merged list. This list merges a plain value, a sub list and
 * and array which is Oddjobs command line arguments.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ListTypeMergeExample.xml}
 * 
 * 
 * @author Rob Gordon.
 */
public class ListType implements ArooaValue, Serializable {
	private static final long serialVersionUID = 20070312;
		
	public static final ArooaElement ELEMENT = new ArooaElement("list");
		
    /**
     * @oddjob.property values
     * @oddjob.description Any values.
     * @oddjob.required No.
     */
	private final List<ArooaValue> values = 
		new ArrayList<ArooaValue>();
	        
    /**
     * @oddjob.property
     * @oddjob.description If the element is a list or array
	 * the values are merged into this list.
     * @oddjob.required No, defaults to not merging.
     */
	private boolean merge;
		
	/** 
	 * @oddjob.property
	 * @oddjob.description Ensures the list contains only
	 * unique elements.
	 * @oddjob.required No.
	 */
	private boolean unique;
	
    /**
     * @oddjob.property
     * @oddjob.description The required element type. If this is specified
     * all elements of the array will attempt to be converted to this type.
     * @oddjob.required No. Elements will be left being what they want to
     * be.
     */
	private Class<?> elementType;
	
	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ListType.class, 
					new Joker<ListType>() {
				public <T> ConversionStep<ListType, T> lastStep(
						Class<? extends ListType> from, 
						final Class<T> to, 
						ConversionLookup conversions) {

			    	if (to.isAssignableFrom(List.class)) {
						return new ConversionStep<ListType, T>() {
							public Class<ListType> getFromClass() {
								return ListType.class;
							}
							public Class<T> getToClass() {
								return to;
							}
							@SuppressWarnings("unchecked")
							public T convert(ListType from, ArooaConverter converter) 
							throws ArooaConversionException {
								return (T) from.convertContents(converter, Object.class);
							}
						};
			    	}
			    	if (to.isArray()) {
						return new ConversionStep<ListType, T>() {
							public Class<ListType> getFromClass() {
								return ListType.class;
							}
							public Class<T> getToClass() {
								return to;
							}
							@SuppressWarnings("unchecked")
							public T convert(ListType from, ArooaConverter converter) 
							throws ArooaConversionException {
					    		List<?> converted = 
					    			from.convertContentsGenericType(
					    					converter, to.getComponentType());
					    		
								Object newArray = Array.newInstance(
										to.getComponentType(), converted.size());
								for (int i = 0; i < converted.size(); ++i) {
										Array.set(newArray, i, 
												converted.get(i));
								}
								return (T) newArray;
							}
						};
			    	}
			    	return null;
				}
					
			});
		}
	}
		
	private <X> List<X> convertContentsGenericType(ArooaConverter converter, 
			Class<?> required) 
	throws ArooaConversionException {
		
		@SuppressWarnings("unchecked")
		Class<X> trueType = (Class<X>) required;
		
		return convertContents(converter, trueType);
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
	public void setValues(int index, ArooaValue element) {	
		new ListSetterHelper<ArooaValue>(values).set(index, element);
	}

	public ArooaValue getValues(int index) {
		return values.get(index);
	}
	
    /**
     * Convert/merge the elements.
     * 
     * @param acKit The ArooaConversionKit used to convert the
     * 			internal types.
     * @param required The required array class 
     * @return
     * 
     * @throws NoConversionAvailableException
     */
	@SuppressWarnings("unchecked")
	<T> List<T> convertContents(ArooaConverter converter, Class<T> required) 
    throws ArooaConversionException {
		
    	List<T> results= new ArrayList<T>();
    	
		if (this.elementType != null) {
			
			if (
				!required.isAssignableFrom(
						this.elementType)) {
				throw new ArooaConversionException(
					"ListType can't convert to required Array/List of " +
					required.getComponentType() + 
					" because elementType attribute is specified as " +
					this.elementType);
			}
			required = (Class<T>) elementType;
		}
		
    	for (Object element : values) {
    		if (merge) {
 
    			List<T> thingsToMerge = new ToListConverter<T>(
    					required, converter).convert(element);
    				
				for (T subElement : thingsToMerge) {
    				if (!unique || !results.contains(subElement)) {
	    				results.add(subElement);
    				}
				}
    		}
    		else {
	    		// No merge, and so just attempt to convert
	    		T content = converter.convert(
						element, required);
	    		
	    		if (!unique || !results.contains(content)) {
	    			results.add(content);
	    		}
    		}
    	}

    	return results;
    }

	@SuppressWarnings("unchecked")
	static class ToListConverter<T> {
	
		private final Class<T> elementType;
		private final ArooaConverter converter;

		ToListConverter(Class<T> elementType,
				ArooaConverter converter) {
			this.elementType = elementType;
			this.converter = converter;
		}
		
		List<T> convert(Object maybeList) 
		throws NoConversionAvailableException, ConversionFailedException {

			// Use an empty convert just to resolve any ArooaValues we might
			// be dealing with.
			Object from = converter.convert(maybeList, Object.class);
			
			if (from == null) {
				return Arrays.asList((T) null);
			}

			if (from.getClass().isArray()) {

				from = new IterableArray(from);
			}

			if (Iterable.class.isInstance(from)) {

				List<T> results= new ArrayList<T>();

				for (Object element : ((Iterable<?>) from)) {

					T converted =  converter.convert(element, elementType);

					results.add(converted);
				}

				return results;
			}

			return Arrays.asList((T) from);		
		}	
	}
	
    public String toString() {
    	return "List of " + values.size() + " things.";
    }
    
	/**
	 * @return Returns the merge.
	 */
	public boolean isMerge() {
		return merge;
	}
	
	/**
	 * Setter for merge.
	 * 
	 * @param merge
	 */
	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	/**
	 * Set unique.
	 * 
	 * @param unique
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	
	/**
	 * Used to convert an Array to an Iterable.
	 */
	static class IterableArray implements Iterable<Object> {

		private final Object array;
		
		private final int length;
		
		IterableArray(Object array) {
			this.array = array;
			this.length = Array.getLength(array);
		}
		
		public Iterator<Object> iterator() {

			return new Iterator<Object>() {

				private int i;
				
				public boolean hasNext() {
					return i < length;
				}
				
				public Object next() {
					return Array.get(array, i++);
				};
				
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
}
