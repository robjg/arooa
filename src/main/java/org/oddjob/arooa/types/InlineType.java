package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.deploy.annotations.ArooaInterceptor;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.life.ArooaContextAware;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ChildCatcher;

import java.util.Objects;

/**
 * A type that provides configuration. It is very like {@link XMLType} but provides a form for the
 * configuration during design rather than a Text Area for raw XML.
 * <p/>
 * To support the correct root element in the form, {@link DesignFactory}s must provide a
 * {@link ConfigurationDefinition} registered in the session's {@link org.oddjob.arooa.registry.BeanRegistry}
 * with the name given by {@link InlineType#INLINE_CONFIGURATION_DEFINITION}.
 */
@ArooaInterceptor("org.oddjob.arooa.xml.XMLInterceptor")
public class InlineType implements ArooaContextAware, ValueFactory<ArooaConfiguration> {

    public static final ArooaElement ELEMENT = new ArooaElement("inline");

    public static final String INLINE_CONFIGURATION_DEFINITION =
            "InlineConfigurationDefinition";

    private ArooaContext arooaContext;

    @Override
    public void setArooaContext(ArooaContext context) {
        this.arooaContext = context;
    }

    @Override
    public ArooaConfiguration toValue() {
        ArooaContext childContext =
                new ChildCatcher<>(this.arooaContext, 0).getChild();

        if (childContext == null) {
            return null;
        }

        return childContext.getConfigurationNode();
    }

    /**
     * Provide information so that the Design Form knows what design the root should be.
     * <p/>
     * This provides the same information as {@link org.oddjob.arooa.parsing.ConfigurationOwner} so
     * this will probably be amalgamated soon.
     */
    public interface ConfigurationDefinition {

        /**
         * Provide the root {@link DesignFactory}.
         *
         * @return A Design Factory. Must not be null.
         */
        DesignFactory rootDesignFactory();

        /**
         * Get the root element.
         *
         * @return The root element of the configuration. Must not be null.
         */
        ArooaElement rootElement();
    }

    public static ConfigurationDefinition configurationDefinition(
            ArooaElement rootElement, DesignFactory designFactory) {

        Objects.requireNonNull(rootElement);
        Objects.requireNonNull(designFactory);

        return new ConfigurationDefinition() {
            @Override
            public DesignFactory rootDesignFactory() {
                return designFactory;
            }

            @Override
            public ArooaElement rootElement() {
                return rootElement;
            }
        };
    }
}

