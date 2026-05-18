package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.oddjob.arooa.convert.gremlin.Gremlin;
import org.oddjob.arooa.convert.gremlin.GremlinSupplier;
import org.oddjob.arooa.utils.TypeToken;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

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

        ConversionPath<GremlinSupplier, ?> conversionToObject1
                = conversionRegistry1.findConversion(GremlinSupplier.class, Object.class);
        assertThat(conversionToObject1.toString(), is(""));
        ConversionPath<GremlinSupplier, ?> conversionToGremlin1
                = conversionRegistry1.findConversion(GremlinSupplier.class, Gremlin.class);
        assertThat(conversionToGremlin1.toString(), is("GremlinSupplier-Gremlin"));

        ConversionPath<GremlinSupplier, ?> conversionToObject2
                = conversionRegistry2.findConversion(GremlinSupplier.class, Object.class);
        assertThat(conversionToObject2.toString(), is("GremlinSupplier-Gremlin"));
        ConversionPath<GremlinSupplier, ?> conversionToGremlin2 = conversionRegistry2.findConversion(GremlinSupplier.class, Object.class);
        assertThat(conversionToGremlin2.toString(), is("GremlinSupplier-Gremlin"));

        GremlinSupplier in = new GremlinSupplier();
        in.setName("Gizmo");

        DefaultConverter converter1 = new DefaultConverter(conversionRegistry1);

        assertThat(conversionToObject1.convert(in, converter1), is(in));
        assertThat(conversionToGremlin1.convert(in, converter1), instanceOf(Gremlin.class));

        DefaultConverter converter2 = new DefaultConverter(conversionRegistry2);

        assertThat(conversionToObject2.convert(in, converter2), instanceOf(Gremlin.class));
        assertThat(conversionToGremlin2.convert(in, converter2), instanceOf(Gremlin.class));
    }

    public static class ListSupplier implements Supplier<List<String>> {

        @Override
        public List<String> get() {
            return List.of("A", "B", "C");
        }
    }

    @Test
    void genericReturnType() throws NoSuchMethodException, ConversionFailedException {

        Method method = ListSupplier.class.getMethod("get");

        ReflectionConversionProvider test = new ReflectionConversionProvider(
                ListSupplier.class, method, true);

        ConversionProvider conversionProvider = test.createConversionProvider(ListSupplier.class.getClassLoader());
        DefaultConversionRegistry conversionRegistry = new DefaultConversionRegistry();

        conversionProvider.registerWith(conversionRegistry);

        ConversionPath<ListSupplier, ?> conversionToObject
                = conversionRegistry.findConversion(ListSupplier.class, Object.class);
        assertThat(conversionToObject.toString(), is("ListSupplier-List"));

        ConversionPath<ListSupplier, ?> conversionToStringList
                = conversionRegistry.findConversion(ListSupplier.class,
                new TypeToken<List<String>>() {}.getType());
        assertThat(conversionToStringList.toString(), is("ListSupplier-List"));

        ConversionPath<ListSupplier, ?> conversionToRawList
                = conversionRegistry.findConversion(ListSupplier.class, List.class);
        assertThat(conversionToRawList.toString(), is("ListSupplier-List"));

        DefaultConverter converter = new DefaultConverter(conversionRegistry);

        Object list = conversionToObject.convert(new ListSupplier(), converter);

        assertThat(list, is(List.of("A", "B", "C")));
    }
}