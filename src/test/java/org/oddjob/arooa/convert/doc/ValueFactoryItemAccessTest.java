package org.oddjob.arooa.convert.doc;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.types.ValueFactory;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ValueFactoryItemAccessTest {

    static class OurValueFactory implements ValueFactory<Integer> {
        @Override
        public Integer toValue() throws ArooaConversionException {
            return 42;
        }
    }

    @Test
    void whenValueFactoryThenItemFound() throws NoSuchMethodException {

        ConversionItemProvider<String> provider = mock(ConversionItemProvider.class);
        when(provider.create(any(), any(), any())).thenReturn("Foo");
        ValueFactoryItemAccess<String> test = new ValueFactoryItemAccess<>(provider);

        TypeIdentifier typeIdentifier = TypeIdentifier
                .ofClass(OurValueFactory.class);

        assertThat(test.containsForType(typeIdentifier), is(true));

        assertThat(test.getForType(typeIdentifier), is("Foo"));

        assertThat(test.containsForType(TypeIdentifier.ofClass(String.class)),
                is(false));

        Method someMethod = ValueFactoryItemAccessTest.class.getDeclaredMethod("whenValueFactoryThenItemFound");

        assertThat(test.containsForType(TypeIdentifier.ofClass(String.class)),
                is(false));
    }

    @Test
    void strategy() {

        ItemAccessStrategy strategy = ValueFactoryItemAccess.strategy;

        StrategyContext<String> context = mock(StrategyContext.class);
        doAnswer(invocation -> {
            return ((Supplier) invocation.getArguments()[1]).get();
        }).when(context)
                .supplyIfAbsent(eq(ValueFactoryItemAccess.class), any());
        ConversionItemProvider<String> factory = mock(ConversionItemProvider.class);

        ConversionRegistry registry = strategy.processIn(context, factory);
        assertThat(registry, notNullValue());

    }
}