package org.oddjob.arooa.forms;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DescriptorDesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignSeedContext;
import org.oddjob.arooa.design.DynamicDesignInstance;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;

public class FormsLookupFromDescriptor implements FormsLookup {

    private final ArooaSession session;

    public FormsLookupFromDescriptor(ArooaDescriptor descriptor) {
        this.session = new StandardArooaSession(descriptor);
    }

    @Override
    public ArooaConfiguration formFor(Object component) {
        return null;
    }

    @Override
    public ArooaConfiguration blankForm(ArooaType arooaType, String elementQualifiedName, String propertyClass) {

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
