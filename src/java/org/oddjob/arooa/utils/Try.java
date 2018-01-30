package org.oddjob.arooa.utils;

import java.util.function.Function;

/**
 * A Functional Success/Failure thing.
 * 
 * @author rob
 *
 * @param <T>
 */
public abstract class Try<T> {

	public static <U> Try<U> of(U value) {
		return new Success<>(value);
	}

	public static <U> Try<U> ofNonNull(U value, String m) {
		if (value == null) {
			return new Failure<>(new NullPointerException(m));
		}
		else {
			return new Success<>(value);
		}
	}

	public static <U> Try<U> fail(Exception e) {
		return new Failure<>(e);
	}
	
	abstract public T orElseThrow() throws Exception;
	
	abstract public <U> Try<U> map(Function<T, U> f);
	
	abstract public <U> Try<U> flatMap(Function<T, Try<U>> f);
	
	abstract public <U> Try<U> trying(Func<T, U, ?> f);
	
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
			} catch (Exception e) {
				return new Failure<>(e);
			}
		}
	}
	
	private static class Failure<T> extends Try<T> {

		private final Exception e;
		
		Failure(Exception e) {
			this.e = e;
		}
		
		@Override
		public T orElseThrow() throws Exception {
			throw e;
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
		
	}

	@FunctionalInterface
	public interface Func<F, T, E extends Exception> {

		T apply(F from) throws E;
	}
	
}