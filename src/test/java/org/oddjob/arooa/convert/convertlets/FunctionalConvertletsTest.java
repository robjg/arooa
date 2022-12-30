package org.oddjob.arooa.convert.convertlets;

import org.junit.Test;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class FunctionalConvertletsTest {

    @Test
    public void testFunctionToPredicate() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new FunctionalConvertlets().registerWith(registry);

        ConversionPath<Function, Predicate> path = registry.findConversion(
                Function.class, Predicate.class);

        Predicate predicate = path.convert(x -> Integer.valueOf(5).equals(x), null);

        assertThat(predicate.test(5), is(true));
        assertThat(predicate.test(4), is(false));

        Predicate predicate2 = path.convert(x -> x == "Foo", null);

        assertThat(predicate2.test("Foo"), is(true));
        assertThat(predicate2.test("Bar"), is(false));
    }

    @Test
    public void testFunctionToConsumer() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new FunctionalConvertlets().registerWith(registry);

        ConversionPath<Function, Consumer> path = registry.findConversion(
                Function.class, Consumer.class);

        List<Object> results = new ArrayList<>();

        Consumer consumer = path.convert(x -> results.add(x), null);

        consumer.accept(5);
        consumer.accept(4);

        Consumer consumer2 = path.convert(x -> results.add(x), null);

        consumer2.accept("Foo");
        consumer2.accept("Bar");

        assertThat(results, contains(5, 4, "Foo", "Bar"));
    }

    @Test
    public void testFunctionToSupplier() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new FunctionalConvertlets().registerWith(registry);

        ConversionPath<Function, Supplier> path = registry.findConversion(
                Function.class, Supplier.class);

        Queue<Object> results = new ConcurrentLinkedQueue();
        results.add(5);
        results.add(4);
        results.add("Foo");
        results.add("Bar");

        Supplier supplier = path.convert(x -> results.poll(), null);

        assertThat(supplier.get(), is(5));
        assertThat(supplier.get(), is(4));

        assertThat(supplier.get(), is("Foo"));
        assertThat(supplier.get(), is("Bar"));
    }
}