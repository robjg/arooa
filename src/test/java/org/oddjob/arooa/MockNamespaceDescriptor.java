package org.oddjob.arooa;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.URI;
import java.util.Objects;

public class MockNamespaceDescriptor extends MockArooaDescriptor {

    private final NamespaceMappings namespaceMappings;

    public MockNamespaceDescriptor(NamespaceMappings namespaceMappings) {
        this.namespaceMappings = Objects.requireNonNull(namespaceMappings);
    }

    @Override
    public ConversionProvider getConvertletProvider() {
        return null;
    }

    @Override
    public ElementMappings getElementMappings() {
        return null;
    }

    @Override
    public ArooaBeanDescriptor getBeanDescriptor(ArooaClass classIdentifier, PropertyAccessor accessor) {
        return null;
    }

    @Override
    public String getPrefixFor(URI namespace) {
        return namespaceMappings.getPrefixFor(namespace);
    }

    @Override
    public String[] getPrefixes() {
        return namespaceMappings.getPrefixes();
    }

    @Override
    public URI getUriFor(String prefix) {
        return namespaceMappings.getUriFor(prefix);
    }
}
