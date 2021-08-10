package org.oddjob.arooa.convert.gremlin;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;

public class GremlinConversions implements ConversionProvider {

    @Override
    public void registerWith(ConversionRegistry registry) {
        registry.register(GremlinSupplier.class, Gremlin.class,
                GremlinSupplier::get);
    }
}
