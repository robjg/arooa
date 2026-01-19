package org.oddjob.arooa.convert.doc;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.Joker;

import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class DocOrganiserTest {

    static class OurConvertlet implements Convertlet<Integer, String> {
        @Override
        public String convert(Integer from) throws ArooaConversionException {
            return from.toString();
        }
    }

    record Foo(Type from, Type to, Class<?> type) {

    }

    static class FooFactory implements ConversionItemProvider<Foo> {

        @Override
        public Foo forConvertlet(Type from, Type to, Convertlet<?, ?> convertlet) {
            return new Foo(from, to , convertlet.getClass());
        }

        @Override
        public Foo forJoker(Type from, Joker<?> joker) {
            throw new RuntimeException("Unexpected");
        }
    }

    @Test
    void addAndGet() {

        ConversionOrganiser<Foo> test = new ConversionOrganiser<>();

        StandardItemAccess.strategy.processIn(test, new FooFactory())
                .register(Integer.class, String.class, new OurConvertlet());

        assertThat(test.containsForType(OurConvertlet.class.getCanonicalName()),
                is(true));

        Foo item = test.getForType(OurConvertlet.class.getCanonicalName());

        assertThat(item.type(), is(OurConvertlet.class));
        assertThat(item.from(), is(Integer.class));
        assertThat(item.to(), is(String.class));

        List<Foo> all = test.getAll();
        assertThat(all, contains(item));
    }
}