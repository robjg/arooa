package org.oddjob.arooa.forms;

import org.oddjob.arooa.*;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;

public class FormsLookupFromDescriptor implements FormsLookup {

    private final ArooaDescriptor descriptor;

    public FormsLookupFromDescriptor(ArooaDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public ArooaConfiguration formFor(ArooaConfiguration configuration) throws ArooaParseException {

        ArooaSession session = new StandardArooaSession(descriptor);

        DesignParser parser = new DesignParser(session);
        parser.setArooaType(ArooaType.COMPONENT);

        parser.parse(configuration);

        DesignInstance designInstance = parser.getDesign();

        return new DesignToFormConfig().configurationFor(designInstance);
    }

    @Override
    public ArooaConfiguration blankForm(ArooaType arooaType, String elementQualifiedName, String propertyClass) {

        ArooaSession session = new StandardArooaSession(descriptor);

        Class<?> cl = session.getArooaDescriptor()
                .getClassResolver()
                .findClass(propertyClass);

        if (cl == null) {
            throw new IllegalArgumentException("No class for " + propertyClass);
        }

        ArooaClass arooaClass = new SimpleArooaClass(cl);

        ArooaElement element = session.getArooaDescriptor().elementFor(elementQualifiedName);

        DesignSeedContext seedContext = new DesignSeedContext(arooaType, session);

        DesignInstance designInstance = new DescriptorDesignFactory(
                new InstantiationContext(arooaType, arooaClass))
                .createDesign(element, seedContext);

        if (designInstance instanceof DynamicDesignInstance) {
            ((DynamicDesignInstance) designInstance).setClassName(
              propertyClass);
        }

        return new DesignToFormConfig().configurationFor(designInstance);
    }

}
