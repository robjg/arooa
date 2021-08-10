package org.oddjob.arooa.convert;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.oddjob.arooa.convert.gremlin.Gremlin;
import org.oddjob.arooa.convert.gremlin.GremlinSupplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReflectionConversionProviderTest {

    @Test
    public void testConversionProviderFromSupplier() throws NoSuchMethodException, ArooaConversionException {

        ReflectionConversionProvider test = new ReflectionConversionProvider(
                GremlinSupplier.class, GremlinSupplier.class.getMethod("get"));

        ConversionProvider conversionProvider = test.createConversionProvider(
                getClass().getClassLoader());

        ConversionRegistry conversionRegistry = mock(ConversionRegistry.class);

        conversionProvider.registerWith(conversionRegistry);

        @SuppressWarnings("unchecked") ArgumentCaptor<Convertlet<GremlinSupplier, Gremlin>> convertletCaptor =
                ArgumentCaptor.forClass(Convertlet.class);

        verify(conversionRegistry).register(eq(GremlinSupplier.class), eq(Gremlin.class),
                convertletCaptor.capture());

        Convertlet<GremlinSupplier, Gremlin> convertlet = convertletCaptor.getValue();

        GremlinSupplier in = new GremlinSupplier();
        in.setName("Gizmo");

        Gremlin gremlin = convertlet.convert(in);

        assertThat(gremlin.getName(), is("Gizmo"));
    }
}