package org.oddjob.arooa.convert;

public class MockConvertletRegistry implements ConversionRegistry {

	@Override
	public <F, T> void register(TypeArooa<F> from, TypeArooa<?> to, Convertlet<F, T> convertlet) {
		throw new RuntimeException("Unexpected from " + getClass());
	}

	public <F> void registerJoker(Class<F> from, Joker<F> joker) {
		throw new RuntimeException("Unexpected from " + getClass());
	}
	
}
