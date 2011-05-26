package org.oddjob.arooa.convert;

public class MockConvertletRegistry implements ConversionRegistry {

	public <F, T> void register(Class<F> from, Class<T> to,
			Convertlet<F, T> convertlet) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
	public <F> void registerJoker(Class<F> from, Joker<F> joker) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
}
