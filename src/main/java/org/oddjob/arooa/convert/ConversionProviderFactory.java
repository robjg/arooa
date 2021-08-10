package org.oddjob.arooa.convert;

/**
 * Provides a {@link ConversionProvider}. This allows an {@link org.oddjob.arooa.ArooaDescriptor}
 * bean to defer creation of the {@link ConversionProvider} until the {@code ClassLoader} is
 * known.
 */
public interface ConversionProviderFactory {

    /**
     * Create an {@link ConversionProvider}.
     *
     * @param classLoader The Class Loader.
     * @return An {@code ConversionProvider}. Never null.
     */
    ConversionProvider createConversionProvider(ClassLoader classLoader);
}
