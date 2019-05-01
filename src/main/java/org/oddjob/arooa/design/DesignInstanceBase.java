/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design;

import org.oddjob.arooa.*;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.Objects;

/**
 * Base implementation for {@link DesignInstance}s.
 */
abstract class DesignInstanceBase implements ParsableDesignInstance {

    private final ArooaElement element;

    private final ArooaContext context;

    /**
     * Utility method for discovering the {@link ArooaClass} for an
     * {@link ArooaElement}.
     */
    static class ClassFinder {
        ArooaClass forElement(ArooaElement element,
                              ArooaContext parentContext) {
            ArooaClass theClass = null;

            ArooaDescriptor descriptor =
                    parentContext.getSession().getArooaDescriptor();

            ElementMappings mappings =
                    descriptor.getElementMappings();

            if (mappings != null) {
                theClass = mappings.mappingFor(element,
                        new InstantiationContext(parentContext));
            }

            if (theClass == null) {
                throw new NullPointerException("No Class for Element [" +
                        element + "] of type " + parentContext.getArooaType());
            }

            return theClass;
        }
    }

    /**
     * Constructor.
     *
     * @param element
     * @param classIdentifier
     * @param parentContext
     */
    public DesignInstanceBase(ArooaElement element,
                              ArooaClass classIdentifier, ArooaContext parentContext) {

        Objects.requireNonNull(element, "No element");
        Objects.requireNonNull(classIdentifier, "No Class Identifier");
        Objects.requireNonNull(parentContext, "No Parent Context");

        ArooaSession session = parentContext.getSession();

        PropertyAccessor accessor = parentContext.getSession(
        ).getTools().getPropertyAccessor();

        ArooaBeanDescriptor beanDescriptor =
                session.getArooaDescriptor().getBeanDescriptor(
                        classIdentifier, accessor);

        ArooaAttributes attributes = element.getAttributes();
        for (String attributeName : attributes.getAttributeNames()) {
            if (ArooaConstants.ID_PROPERTY.equals(attributeName)) {
                if (this instanceof DesignComponentBase) {
                    continue;
                }
            }
            if (ArooaConstants.KEY_PROPERTY.equals(attributeName)) {
                continue;
            }

            if (!new BeanDescriptorHelper(
                    beanDescriptor).isAttribute(attributeName)) {

                throw new ArooaException(attributeName + " is not an attribute of " +
                        classIdentifier);
            }
        }

        this.element = element;

        this.context = new DesignInstanceContext(this,
                classIdentifier, parentContext);
    }

    @Override
    public ArooaElement element() {
        return element;
    }

    public QTag tag() {
        return InstanceSupport.tagFor(this);
    }

    @Override
    public ArooaContext getArooaContext() {
        return context;
    }

    @Override
    public String toString() {
        return tag().toString();
    }
}
