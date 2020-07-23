package org.oddjob.arooa.design;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.*;

import java.util.Optional;

/**
 * Creates an {@link ArooaConfiguration} out of a
 * {@link ParsableDesignInstance} so that a design can be parsed
 * with an {@link ArooaParser}.
 *
 * @author rob
 */
public class DesignConfiguration implements ArooaConfiguration {

    private final ParsableDesignInstance design;

    public DesignConfiguration(ParsableDesignInstance design) {
        this.design = design;
    }

    @Override
    public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
            throws ArooaParseException {

        ArooaElement element = new ArooaElement(
                design.element().getUri(),
                design.element().getTag());

        String key = Optional.ofNullable(design.getArooaContext().getParent())
                .filter(c -> c instanceof PropertyContext)
                .map(c -> (PropertyContext) c)
                .map(pc -> pc.getKey(design))
                .orElse(null);

        if (key != null) {
            element = element.addAttribute(ArooaConstants.KEY_PROPERTY, key);
        }

        parentContext.getPrefixMappings().add(
                design.getArooaContext().getPrefixMappings());

        ElementHandler<P> handler = parentContext.getElementHandler();

        if (design instanceof DesignComponent) {
            String id = ((DesignComponent) design).getId();
            if (id != null && id.length() > 0) {
                element = element.addAttribute(
                        ArooaConstants.ID_PROPERTY, id);
            }
        }

        for (DesignProperty child : design.children()) {
            if (child instanceof DesignAttributeProperty) {

                DesignAttributeProperty attributeProperty =
                        (DesignAttributeProperty) child;

                if (attributeProperty.attribute() != null &&
                        attributeProperty.attribute().length() > 0) {
                    element = element.addAttribute(
                            child.property(), attributeProperty.attribute());
                }
            } else if (child instanceof DesignTextProperty) {
                // Handled as an Element
                continue;
            } else if (child instanceof DesignElementProperty) {
                // Handled as Text
                continue;
            } else {
                throw new IllegalStateException("Unsupported property " + child);
            }
        }

        ParseHandle<P> handle;
        try {
            handle = handler.onStartElement(
                    element, parentContext);
        } catch (ArooaConfigurationException e) {
            throw new ArooaParseException("Failed parsing design.",
                    new Location(design.toString(), 0, 0), e);
        }

        P nextContext = handle.getContext();

        for (DesignProperty child : design.children()) {

            if (child instanceof DesignAttributeProperty) {
                continue;
            } else if (child instanceof DesignTextProperty) {

                DesignTextProperty textProperty =
                        (DesignTextProperty) child;

                if (textProperty.text() != null) {
                    handle.addText(
                            textProperty.text());
                }
            } else if (child instanceof DesignElementProperty) {
                parse(nextContext, (DesignElementProperty) child);
            } else {
                throw new IllegalStateException("Unsupported property " + child);
            }
        }

        try {
            handle.init();
        } catch (RuntimeException e) {
            throw new ArooaParseException("Failed parsing design.",
                    new Location(design.toString(), 0, 0), e);
        }

        return new ContextConfigurationHandle<>(nextContext);
    }

    static class ContextConfigurationHandle<P extends ParseContext<P>> implements ConfigurationHandle<P> {

        private final P nextContext;

        public ContextConfigurationHandle(P nextContext) {
            this.nextContext = nextContext;
        }

        public void save() throws ArooaParseException {
            throw new UnsupportedOperationException(
                    "Not Implemented... should it be?");
        }

        public P getDocumentContext() {
            // Is it possible that this will have changed?
            // Do we need to use a ChildCatcher?
            return nextContext;
        }
    }


    private <P extends ParseContext<P>> ConfigurationHandle<P>
    parse(P parentContext,
          DesignElementProperty designProperty) throws ArooaParseException {

        return designProperty
                .getArooaContext()
                .getConfigurationNode()
                .parse(parentContext);
    }
}
