package org.oddjob.arooa.convert.doc;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.Convertlet;

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

    record Foo(Type from, Type to, TypeIdentifier type) {

    }

    static class FooFactory implements ConversionItemProvider<Foo> {

        @Override
        public Foo create(Type from, Type to, TypeIdentifier typeIdentifier) {
            return new Foo(from, to , typeIdentifier);
        }
    }

    @Test
    void addAndGet() {

        ConversionOrganiser<Foo> test = new ConversionOrganiser<>();

        StandardItemAccess.strategy.processIn(test, new FooFactory())
                .register(Integer.class, String.class, new OurConvertlet());

        assertThat(test.containsForType(TypeIdentifier.ofClass(OurConvertlet.class)),
                is(true));

        Foo item = test.getForType(TypeIdentifier.ofClass(OurConvertlet.class));

        assertThat(item.type(), is(TypeIdentifier.ofClass(OurConvertlet.class)));
        assertThat(item.from(), is(Integer.class));
        assertThat(item.to(), is(String.class));

        List<Foo> all = test.getAll();
        assertThat(all, contains(item));
    }
}