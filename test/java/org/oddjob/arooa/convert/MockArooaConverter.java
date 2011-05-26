package org.oddjob.arooa.convert;

public class MockArooaConverter implements ArooaConverter {

	public <F, T> T convert(F from, Class<T> required)
			throws NoConversionAvailableException, ConversionFailedException {
		throw new RuntimeException("Unexpected from class " + getClass());
	}
	
	public <F, T> ConversionPath<F, T> findConversion(Class<F> from, Class<T> to) {
		throw new RuntimeException("Unexpected from class " + getClass());
	}
}
