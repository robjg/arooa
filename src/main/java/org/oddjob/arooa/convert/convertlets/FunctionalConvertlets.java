package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Conversions to allow a Function to be other some other useful Functional Interfaces. Mainly useful around
 * scripting to allow a Javascript function to be a Java Predicate, Supplier, or Consumer.
 */
public class FunctionalConvertlets implements ConversionProvider {

    @Override
    public void registerWith(ConversionRegistry registry) {

        registry.register(Function.class, Predicate.class,
                func -> new Predicate() {
                    @Override
                    public boolean test(Object o) {
                        return (boolean) func.apply(o);
                    }

                    @Override
                    public String toString() {
                        return "Predicate from Function " + func;
                    }
                });

        registry.register(Function.class, Consumer.class,
                func -> new Consumer() {
                    @Override
                    public void accept(Object o) {
                        func.apply(o);
                    }

                    @Override
                    public String toString() {
                        return "Consumer from Function " + func;
                    }
                });

        registry.register(Function.class, Supplier.class,
                func -> new Supplier() {
                    @Override
                    public Object get() {
                        return func.apply(null);
                    }

                    @Override
                    public String toString() {
                        return "Supplier from Function " + func;
                    }
                });
    }
}
