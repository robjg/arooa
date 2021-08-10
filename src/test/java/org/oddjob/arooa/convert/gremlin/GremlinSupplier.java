package org.oddjob.arooa.convert.gremlin;

import java.util.Objects;
import java.util.function.Supplier;

public class GremlinSupplier implements Supplier<Gremlin>  {

    private String name;

    @Override
    public Gremlin get() {

        String name = Objects.requireNonNull(this.name, "No Name");

        return () -> name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
