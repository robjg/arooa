/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.utils.ClassUtils;

/**
 * Implementation of a ConvertletRegistry.
 * 
 * @author rob
 *
 */
public class DefaultConversionRegistry implements ConversionRegistry, ConversionLookup {
	
	/** Map of from class to possible Map of to class to Convertlet.
	 *  This is linked to preserve registration order during searches. */
	private final Map<Class<?>, Map<Class<?>, Convertlet<?, ?>>> fromMap = 
		new LinkedHashMap<Class<?>, Map<Class<?>, Convertlet <?, ?>>>();
	
	private final JokerMap jokers = 
		new JokerMap();
 	

	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.convert.ConvertletRegistry#register(java.lang.Class, java.lang.Class, org.oddjob.arooa.convert.Convertlet)
	 */
	public <F, T> void register(Class<F> from, Class<T> to, 
			Convertlet<F, T> convertlet) {
		
		Map<Class<?>, Convertlet<?, ?>> toConverters = 
			fromMap.get(from);
		
		if (toConverters == null) {
			toConverters = new LinkedHashMap<Class<?>, Convertlet<?, ?>>();
			fromMap.put(from, toConverters);
		}
		
		toConverters.put(to, convertlet);
	}
	
	public <F> void registerJoker(Class<F> from, Joker<F> joker) {
		jokers.register(from, joker);
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.convert.ConvertletRegistry#findConversion(java.lang.Class, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <F, T> ConversionPath<F, T> findConversion(Class<F> from, Class<T> to) {
		if (to.isPrimitive()) {
			to = (Class<T>) ClassUtils.wrapperClassForPrimitive(to);
		}
		
		return best(from, from, to, DefaultConversionPath.instance(from), 0);
	}
	
	/**
	 * Recursive function to find the best conversion path.
	 * 
	 * @param from
	 * @param to
	 * @param stepsSoFar
	 * @param maxLevels
	 * @return
	 */
	@SuppressWarnings("unchecked")
	<F, X, Y, T> ConversionPath<F, T> best(
			final Class<? extends X> start, final Class<X> from, final Class<T> to, 
			ConversionPath<F, X> stepsSoFar, int maxLevels) {
		
		// have we reached the end of the conversion?
		// Only true for ArooaValues if the required is an ArooaValue
		if (to.isAssignableFrom(from) && 
				(ArooaValue.class.isAssignableFrom(from) == ArooaValue.class.isAssignableFrom(to))) {
			return (ConversionPath<F, T>) stepsSoFar;
		}
		
		Iterable<Joker<X>> jokersMatching = jokers.getMatching(from);
		for (Joker<X> joker : jokersMatching) {
			ConversionStep<X, T> step = joker.lastStep(start, to, this); 

			if (step != null) {
				ConversionPath<F, T> full = stepsSoFar.append(step);
				
				// joker trumps all.
				return full;
			}
		}
		
		// keep track of best levels to save us going down unnecessary paths.
		int bestLevels = maxLevels;		
		ConversionPath<F, T> bestResult = null;
				
		
		// get the converters for the next step.
		Map<Class<?>, Convertlet<?, ?>> toConverters = fromMap.get(from);
		
		if (toConverters != null) {		
			// iterate through all the possible conversions to.
			for (Map.Entry<Class<?>, Convertlet<?, ?>> entry : toConverters.entrySet()) {
	
				// the one to try the next
				final Class<Y> maybeTo = (Class<Y>) entry.getKey();
	
				final Convertlet<X, Y> convertlet = (Convertlet<X, Y>) entry.getValue();
	
				if (convertlet instanceof FinalConvertlet 
						&& !to.equals(maybeTo)) {
					// only use a final convertlet if it converts to 
					// the required class.
					continue;
				}
				
				// work out what the next conversion steps would be
				ConversionStep<X, Y> nextStep = new ConversionStep<X, Y>() {
					public Y convert(X from, ArooaConverter converter)
					throws ArooaConversionException {
						return convertlet.convert(from);
					};
					public Class<X> getFromClass() { return from; }
					public Class<Y> getToClass() { return maybeTo; }
				};
	
				// recursively call. A non null result means we found a match.
				ConversionPath<F, T> result = nextBest(maybeTo, 
						stepsSoFar, nextStep, to, bestLevels, true);
	
				if (result != null) {
					// because of the check for best levels this result must
					// now be the best. So remember it.
					bestResult = result;
					bestLevels = result.length() - stepsSoFar.length();
				}
			}
		}

		// Try a superclass conversion.
		Class<? super X>[] supers = extendsAndImplements(from);
		for (int i = 0; i < supers.length; ++ i){
			final Class<Y> superClass = (Class<Y>) supers[i];
		
			// next conversion steps would be with super class
			ConversionStep<X, Y> nextStep = new ConversionStep<X, Y>() {
				public Y convert(X from, ArooaConverter converter)
				throws ArooaConversionException {
					return (Y) from;
				}
				public Class<X> getFromClass() { return from; }
				public Class<Y> getToClass() { return superClass; }
			};

			// recursively call. A non null result means we found a match.
			ConversionPath<F, T> result  = nextBest((Class<? extends Y>) from, 
					stepsSoFar, nextStep, to, bestLevels, false);
			
			if (result != null) {
				// if the superclass gave us a result it must be a shorter path
				// so use it instead.
				bestResult = result;
			}
		}
		
		return bestResult;
	}

	/**
	 * Utility function to calculate what a class extends and
	 * implements.
	 * 
	 * @param fromClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <S> Class<? super S>[] extendsAndImplements(Class<S> fromClass) {
		List<Class<?>> results = new ArrayList<Class<?>>();
		Class<?> superClass = fromClass.getSuperclass();
		
		// Stop paths from an ArooaValue to a non ArooaValue object.
		if (superClass != null && 
				(ArooaValue.class.isAssignableFrom(fromClass) == ArooaValue.class.isAssignableFrom(superClass))) {
			results.add(superClass);
		}

		// An also stop ArooaValue to none ArooaValue interfaces (mainly for Serializable which would then
		// provide a path to Object
		Class<?>[] interfaces = fromClass.getInterfaces();
		for (Class<?> iface : interfaces) {
			if ((ArooaValue.class.isAssignableFrom(fromClass) == ArooaValue.class.isAssignableFrom(iface))) {
				results.add(iface);
			}
		}
		
		return results.toArray(new Class[0]);
	}
	
	/**
	 * Utility method to create the new ConversionPath.
	 * 
	 * @param stepsSoFar Steps now.
	 * @param nextStep Next Step to try.
	 * @param to The class to convert to.
	 * @param maxLevels The maximum number of levels to try.
	 * 
	 * @return The result.
	 */
	<F, X, Y, T> ConversionPath<F, T> nextBest(
			Class<? extends Y> start, ConversionPath<F, X> stepsSoFar, 
			ConversionStep<X, Y> nextStep, Class<T> to, int maxLevels, boolean allowJokers) {
		
		// maxLevels of one means our previous recursive call
		// actually found a perfect match.
		if (maxLevels == 1) {
			return null;
		}
		
		// check we're not going back on ourselves
		if (stepsSoFar.contains(nextStep.getToClass())) {
				return null;
		}
		
		ConversionPath<F, Y> next = stepsSoFar.append(nextStep);
		
		return best(start, nextStep.getToClass(), to, next, maxLevels - 1);
	}
	
	private static class JokerMap {

		private final Map<Class<?>, List<Joker<?>>> map = 
			new LinkedHashMap<Class<?>, List<Joker<?>>>();
		
		<From> void register(Class<From> from, Joker<From> joker) {
			List<Joker<?>> list = map.get(from);
			if (list == null) {
				list = new ArrayList<Joker<?>>();
				map.put(from, list);
			}
			list.add(joker);
		}

		@SuppressWarnings("unchecked")
		<From> Iterable<Joker<From>> getMatching(Class<From> cl) {
			List<Joker<From>> results = new ArrayList<Joker<From>>();
			List<Joker<?>> jokersAny = map.get(cl);
			if (jokersAny != null) {
				if (jokersAny != null) {
					// Why can't I do results.addAll((List<Joker<From>>)jokersAny)????
					for (Joker<?> jokerAny : jokersAny) {
						Joker<From> joker = (Joker<From>) jokerAny;
						results.add(joker);
					}
				}
			}
			return results;
		}
	}		
}
