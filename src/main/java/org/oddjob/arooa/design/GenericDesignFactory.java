package org.oddjob.arooa.design;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link DesignFactory} that creates a {@link DesignInstance}
 * from the properties of a class and its {@link ArooaBeanDescriptor}.
 *
 * @author rob
 *
 */
public class GenericDesignFactory implements DesignFactory {

    private final ArooaClass arooaClass;

    /**
     * Constructor.
     *
     * @param forClass The class. Must not be null.
     */
    public GenericDesignFactory(ArooaClass forClass) {
        if (forClass == null) {
            throw new NullPointerException("No Class.");
        }
        this.arooaClass = forClass;
    }

    /*
     * (non-Javadoc)
     * @see org.oddjob.arooa.design.DesignFactory#createDesign(boolean, org.oddjob.arooa.parsing.ArooaElement, org.oddjob.arooa.parsing.ArooaContext)
     */
    public DesignInstance createDesign(
            ArooaElement element,
            ArooaContext parentContext)
            throws ArooaPropertyException {

        boolean componentInstance =
                parentContext.getArooaType() == ArooaType.COMPONENT;

        GenericDesignInstance design;
        if (componentInstance) {
            design = new DesignComponentInstance(
                    element, arooaClass, parentContext);
        } else {
            design = new DesignValueInstance(
                    element, arooaClass, parentContext);
        }

        design.children(designProperties(design));

        return design;
    }

    /**
     * Create the {@link DesignProperty}s for a design.
     *
     * @param design The design.
     * @return Array of design properties.
     */
    public DesignProperty[] designProperties(DesignInstance design) {

        ArooaContext parentContext = design.getArooaContext().getParent();

        boolean componentInstance =
                parentContext.getArooaType() == ArooaType.COMPONENT;

        ArooaSession session = parentContext.getSession();

        PropertyAccessor accessor = session.getTools().getPropertyAccessor();

        BeanOverview overview = arooaClass.getBeanOverview(
                accessor);

        List<DesignProperty> designProperties =
                new ArrayList<>();

        List<String> properties = new ArrayList<>(
                Arrays.asList(overview.getProperties()));

        if (componentInstance) {
            properties.remove("id");
        }

        ArooaBeanDescriptor arooaBeanDescriptor =
                session.getArooaDescriptor().getBeanDescriptor(
                        arooaClass, accessor);

        for (String property : properties) {

            if (!overview.hasWriteableProperty(property)) {
                continue;
            }

            Class<?> propertyClassName = overview.getPropertyType(property);

            ConfiguredHow configuredHow = arooaBeanDescriptor
                    .getConfiguredHow(property);

            if (configuredHow == ConfiguredHow.HIDDEN) {
                continue;
            }

            if (configuredHow == ConfiguredHow.ATTRIBUTE) {

                designProperties.add(new SimpleTextAttribute(property, design));
                continue;
            }

            if (configuredHow == ConfiguredHow.TEXT) {

                designProperties.add(new SimpleTextProperty(property));
                continue;
            }

            ArooaType type = arooaBeanDescriptor.getArooaType(property);

            DesignElementProperty elementProperty;

            if (overview.isIndexed(property)) {
                elementProperty = new IndexedDesignProperty(
                        property, propertyClassName, type, design);
            } else if (overview.isMapped(property)) {
                elementProperty = new MappedDesignProperty(
                        property, propertyClassName, type, design);
            } else {
                elementProperty = new SimpleDesignProperty(
                        property, propertyClassName, type, design);
            }

            designProperties.add(elementProperty);
        }

        return designProperties.toArray(new DesignProperty[0]);
    }

}
