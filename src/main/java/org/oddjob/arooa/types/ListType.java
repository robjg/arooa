package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.Configured;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.utils.EtcUtils;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @oddjob.description A list provides a way of setting properties that are
 * either {@link java.util.List} types or arrays. A list can include any other 
 * type including another list or array type.
 * <p>
 * Handling of multidimensional arrays has not been considered. Such properties
 * are probably best defined with a custom {@link org.oddjob.arooa.ArooaValue}.
 * 
 * @oddjob.example
 * 
 * A simple list of things. The list contains 3 things two Strings and a 
 * nested list that contains one String.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ListSimpleWithNestedList.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/ListSimpleWithNestedListOut.txt}
 * 
 * @oddjob.example
 * 
 * A Merged list. This list merges a plain value, a sub list
 * and array into a list of 5 separate values.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ListTypeMergeExample.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/ListTypeMergeExampleOut.txt}
 * 
 * @oddjob.example
 * 
 * A Converted list. The elements of the list are converted to an array of
 * Strings.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ListWithConversion.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/ListWithConversionOut.txt}
 * 
 * Although it can't be seen in the output, but can be seen when this
 * example is run in Oddjob Explorer, the list contains to String array 
 * elements.
 * 
 * @oddjob.example
 * 
 * Add to a list the fly. This example demonstrates setting the 
 * hidden 'add' property. The property is hidden so that it can't be set
 * via configuration which could be confusing. A side effect of this is that
 * it is also hidden from the Reference Guide generator.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ListTypeAddWithSet.xml}
 * 
 * The output is:
 * 
 * {@oddjob.text.resource org/oddjob/arooa/types/ListTypeAddWithSetOut.txt}
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
			new ArrayList<>();
	        
	/** Values added after configuration. */
	private final List<ArooaValue> extras =
			new ArrayList<>();
	
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
					new Joker<>() {
						public <T> ConversionStep<ListType, T> lastStep(
								Class<? extends ListType> from,
								final Class<T> to,
								ConversionLookup conversions) {

							if (to.isAssignableFrom(List.class)) {
								return new ConversionStep<>() {
									public Class<ListType> getFromClass() {
										return ListType.class;
									}

									public Class<T> getToClass() {
										return to;
									}

									@SuppressWarnings("unchecked")
									public T convert(ListType from, ArooaConverter converter)
											throws ArooaConversionException {

										return (T) from.convertContents(
												converter, Object.class);
									}
								};
							}
							if (to.isArray()) {
								return new ConversionStep<>() {
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
							if (to.isAssignableFrom(Consumer.class)) {
								return new ConversionStep<>() {
									public Class<ListType> getFromClass() {
										return ListType.class;
									}

									public Class<T> getToClass() {
										return to;
									}

									@SuppressWarnings("unchecked")
									public T convert(ListType from, ArooaConverter converter) {
										return (T) new ListConsumer(from, converter);
									}
								};
							}
							return null;
						}


					});

			registry.register(ListConsumer.class, ListType.class,
					from -> from.listType);
		}
	}

	public static class ListConsumer implements Consumer<Object> {

		private final ListType listType;

		private final ArooaConverter arooaConverter;

		private ListConsumer(ListType listType, ArooaConverter arooaConverter) {
			this.listType = listType;
			this.arooaConverter = arooaConverter;
		}

		@Override
		public void accept(Object element) {
			listType.setAdd(new ArooaObject(element));
		}

		public List<Object> getValues() throws ArooaConversionException {
			return listType.convertContents(arooaConverter, Object.class);
		}

		public Object getValue(int index) throws NoConversionAvailableException, ConversionFailedException {
			ArooaValue arooaValue = index < listType.values.size() ?
					listType.values.get(index) : listType.extras.get(index - listType.values.size());

			return arooaConverter.convert(arooaValue, Object.class);
		}

		@Override
		public String toString() {
			return "ListConsumer of " + listType;
		}
	}


	/**
	 * Converts to a list of a given generic type.
	 * 
	 * @param converter
	 * @param required
	 * @return
	 * 
	 * @throws ArooaConversionException
	 */
	private <X> List<X> convertContentsGenericType(ArooaConverter converter, 
			Class<?> required) 
	throws ArooaConversionException {
		
		@SuppressWarnings("unchecked")
		Class<X> trueType = (Class<X>) required;
		
		return convertContents(converter, trueType);
	}
	
	@Configured
	public void configured() {
		synchronized (values) {
			extras.clear();
		}
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
		synchronized (values) {
			new ListSetterHelper<>(values).set(index, element);
		}
	}

	public ArooaValue getValues(int index) {
		synchronized (values) {
			return values.get(index);
		}
	}
	
    /**
     * Convert/merge the elements.
     * 
     * @param converter The converter used to convert the
     * 			internal types.
     * @param required The required array class 
     * @return
     * 
     * @throws NoConversionAvailableException
     */
	@SuppressWarnings("unchecked")
	<T> List<T> convertContents(ArooaConverter converter, Class<T> required) 
    throws ArooaConversionException {
		
    	List<T> results= new ArrayList<>();
    	
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
		
		List<ArooaValue> valuesAndExtras = new ArrayList<>();
		synchronized (values) {
			valuesAndExtras.addAll(values);
			valuesAndExtras.addAll(extras);
		}
		
    	for (Object element : valuesAndExtras) {
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
				return Collections.singletonList(null);
			}

			if (from.getClass().isArray()) {

				from = new IterableArray(from);
			}

			if (from instanceof Iterable) {

				List<T> results= new ArrayList<>();

				for (Object element : ((Iterable<?>) from)) {

					T converted =  converter.convert(element, elementType);

					results.add(converted);
				}

				return results;
			}

			return Collections.singletonList((T) from);
		}	
	}

	@Override
    public String toString() {
		Stream<Object> all = Stream.concat(values.stream(), extras.stream());

    	return EtcUtils.toString(all::iterator,
				values.size() + extras.size());
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
	
	@ArooaHidden
	public void setAdd(ArooaValue value) {
		synchronized (values) {
			this.extras.add(value);
		}
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

			return new Iterator<>() {

				private int i;

				public boolean hasNext() {
					return i < length;
				}

				public Object next() {
					return Array.get(array, i++);
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
}
