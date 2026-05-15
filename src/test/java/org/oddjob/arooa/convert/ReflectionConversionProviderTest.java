package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.oddjob.arooa.convert.gremlin.Gremlin;
import org.oddjob.arooa.convert.gremlin.GremlinSupplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReflectionConversionProviderTest {

    @Test
    void testConversionProviderFromSupplier() throws NoSuchMethodException, ArooaConversionException {

        ReflectionConversionProvider test = new ReflectionConversionProvider(
                GremlinSupplier.class, GremlinSupplier.class.getMethod("get"),
                false);

        ConversionProvider conversionProvider = test.createConversionProvider(
                getClass().getClassLoader());

        ConversionRegistry conversionRegistry = mock(ConversionRegistry.class);

        conversionProvider.registerWith(conversionRegistry);

        @SuppressWarnings("unchecked") ArgumentCaptor<Convertlet<GremlinSupplier, Gremlin>> convertletCaptor =
                ArgumentCaptor.forClass(Convertlet.class);

        verify(conversionRegistry).register(any(TypeArooa.class), any(TypeArooa.class),
                convertletCaptor.capture());

        Convertlet<GremlinSupplier, Gremlin> convertlet = convertletCaptor.getValue();

        GremlinSupplier in = new GremlinSupplier();
        in.setName("Gizmo");

        Gremlin gremlin = convertlet.convert(in);

        assertThat(gremlin.getName(), is("Gizmo"));
    }

    @Test
    void conversionForArooaValue() throws NoSuchMethodException, ArooaConversionException {

        ReflectionConversionProvider test1 = new ReflectionConversionProvider(
                GremlinSupplier.class, GremlinSupplier.class.getMethod("get"),
                false);

        ReflectionConversionProvider test2 = new ReflectionConversionProvider(
                GremlinSupplier.class, GremlinSupplier.class.getMethod("get"),
                true);

        ConversionProvider conversionProvider1 = test1.createConversionProvider(
                getClass().getClassLoader());

        ConversionProvider conversionProvider2 = test2.createConversionProvider(
                getClass().getClassLoader());

        DefaultConversionRegistry conversionRegistry1 = new DefaultConversionRegistry();
        DefaultConversionRegistry conversionRegistry2 = new DefaultConversionRegistry();

        conversionProvider1.registerWith(conversionRegistry1);
        conversionProvider2.registerWith(conversionRegistry2);

        ConversionPath<GremlinSupplier, ?> conversion1 = conversionRegistry1.findConversion(GremlinSupplier.class, Object.class);
        assertThat(conversion1.toString(), is(""));

        ConversionPath<GremlinSupplier, ?> conversion2 = conversionRegistry2.findConversion(GremlinSupplier.class, Object.class);
        assertThat(conversion2.toString(), is("GremlinSupplier-Gremlin"));

        GremlinSupplier in = new GremlinSupplier();
        in.setName("Gizmo");

        DefaultConverter converter = new DefaultConverter(conversionRegistry1);

        assertThat(conversion1.convert(in, converter), is(in));
        assertThat(conversion2.convert(in, converter), instanceOf(Gremlin.class));
    }

}