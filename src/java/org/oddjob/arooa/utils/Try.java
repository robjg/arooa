package org.oddjob.arooa.utils;

import java.util.Objects;
import java.util.function.Function;

/**
 * A Functional Success/Failure thing.
 * 
 * @author rob
 *
 * @param <T>
 */
public abstract class Try<T> {

	/**
	 * Create a Try from a value. At the moment the value may be null. Not sure if this is a good idea.
	 * 
	 * @param value A value. May be null.
	 * 
	 * @return A Try wrapping the value.
	 */
	public static <U> Try<U> of(U value) {
		return new Success<>(value);
	}

	/**
	 * A try with a null check. If the value is null, the Try is a wrapped NullPointerException.
	 * 
	 * @param value The value.
	 * @param m A message that will be added to the NullPointerException if the value is null. May be null.
	 * 
	 * @return A Try wrapping the value or a NullPointerException.
	 */
	public static <U> Try<U> ofNonNull(U value, String m) {
		if (value == null) {
			return new Failure<>(new NullPointerException(m));
		}
		else {
			return new Success<>(value);
		}
	}

	/**
	 * Create a try wrapping an Exception.
	 * 
	 * @param e The Exception. Must not be null.
	 * 
	 * @return A Try wrapping the Exception.
	 */
	public static <U> Try<U> fail(Exception e) {
		return new Failure<>(e);
	}
	
	/**
	 * Recover the value or throw an Exception.
	 * 
	 * @return The value. May be null.
	 * 
	 * @throws Exception if the Try resulted in a Failure.
	 */
	abstract public T orElseThrow() throws Exception;

	/**
	 * Recover the value or apply a function to the exception to 
	 * get result.
	 * 
	 * @param f A function taking the exception.
	 * 
	 * @return A value.
	 */
	abstract public T orElse(Function<Exception, T> f);
	
	/**
	 * Apply a function to the Try. The function will only be applied if the Try is currently a Success.
	 * 
	 * @param f The Function to apply.
	 * 
	 * @return A new Try wrapping the result of applying the function, or a previous Failure.
	 */
	abstract public <U> Try<U> map(Function<T, U> f);
	
	/**
	 * Apply a function to the Try that itself returns a Try. The function will only be applied if the 
	 * Try is currently a Success.
	 * 
	 * @param f The Function to apply. 
	 * 
	 * @return A new Try wrapping the result of applying the function, or a previous Failure, or a new
	 * Failure from the function.
	 */
	abstract public <U> Try<U> flatMap(Function<T, Try<U>> f);
	
	/**
	 * Try a Function that might throw a Exception.
	 * 
	 * @param f The Function to apply. 
	 * 
	 * @return A new Try wrapping the result of applying the function, or a previous Failure, or a new
	 * Failure from the function.
	 */
	abstract public <U> Try<U> trying(Func<T, U, ?> f);	
	
	/**
	 * Success.
	 * 
	 * @param <T>
	 */
	private static class Success<T> extends Try<T> {
		
		private final T value;
		
		Success(T value) {
			this.value = value;
		}
		
		@Override
		public T orElseThrow() throws RuntimeException {
			return value;
		}

		
		@Override
		public T orElse(Function<Exception, T> f) {
			return value;
		}
		
		@Override
		public <U> Try<U> map(Function<T, U> f) {
			return new Success<>(f.apply(value));
		}

		@Override
		public <U> Try<U> flatMap(Function<T, Try<U>> f) {
			return f.apply(value);
		}
		
		@Override
		public <U> Try<U> trying(Func<T, U, ?> f) {
			
			try {
				return new Success<>(f.apply(value));
			} 
			catch (Exception e) {
				return new Failure<>(e);
			}
		}
		
		@Override
		public int hashCode() {
			return value == null ? 0 : value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			if (this.getClass() !=  obj.getClass()) {
				return false;
			}
	
			Success<?> other = (Success<?>) obj;

			if (this.value == null) {
				return other.value == null;
			}
			
			return this.value.equals(other.value);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + value;
		}
	}
	
	/**
	 * Failure.
	 * 
	 * @param <T>
	 */
	private static class Failure<T> extends Try<T> {

		private final Exception e;
		
		Failure(Exception e) {
			Objects.requireNonNull(e);
			this.e = e;
		}
		
		@Override
		public T orElseThrow() throws Exception {
			throw e;
		}

		@Override
		public T orElse(Function<Exception, T> f) {
			return f.apply(e);
		}
		
		
		@SuppressWarnings("unchecked")
		@Override
		public <U> Try<U> map(Function<T, U> f) {
			return (Try<U>) this;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <U> Try<U> flatMap(Function<T, Try<U>> f) {
			return (Try<U>) this;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <U> Try<U> trying(Func<T, U, ?> f) {
			return (Try<U>) this;
		}
		
		@Override
		public int hashCode() {
			return e.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			if (this.getClass() !=  obj.getClass()) {
				return false;
			}
	
			Failure<?> other = (Failure<?>) obj;
			
			return this.e.equals(other.e);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + e;
		}
	}

	/**
	 * A Function that throws an Exception. Not very functional - but useful.
	 *
	 * @param <T> the type of the input to the function
	 * @param <R> the type of the result of the function
	 * @param <E> the type of the exception.
	 */
	@FunctionalInterface
	public interface Func<T, R, E extends Exception> {

		R apply(T from) throws E;

	}
	
}