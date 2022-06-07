package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.deploy.annotations.*;
import org.oddjob.arooa.utils.ClassUtils;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Contributes to an {@link BeanDescriptorContributor} from an annotated class.
 *
 * @author rob
 */
public class AnnotatedBeanDescriptorContributor implements BeanDescriptorContributor {

    public void makeContribution(ArooaAnnotationsHelper annotations,
                                 BeanDescriptorAccumulator accumulator) {

        Class<?> cl = accumulator.getClassIdentifier().forClass();

        ArooaInterceptor interceptorAnnotation = cl.getAnnotation(ArooaInterceptor.class);
        if (interceptorAnnotation != null) {

            String interceptor = interceptorAnnotation.value();
            if (interceptor.length() > 0) {
                ParsingInterceptor parsingInterceptor = (ParsingInterceptor)
                        ClassUtils.instantiate(
                                interceptor, cl.getClassLoader());
                accumulator.setParsingInterceptor(parsingInterceptor);
            }
        }

        String[] properties = annotations.annotatedProperties();

        for (String property : properties) {

            if (annotations.annotationForProperty(
                    property, ArooaAttribute.class.getName()) != null) {
                accumulator.addAttributeProperty(property);
            }

            if (annotations.annotationForProperty(
                    property, ArooaElement.class.getName()) != null) {
                accumulator.addElementProperty(property);
            }

            if (annotations.annotationForProperty(
                    property, ArooaHidden.class.getName()) != null) {
                accumulator.addHiddenProperty(property);
            }

            if (annotations.annotationForProperty(
                    property, ArooaText.class.getName()) != null) {
                accumulator.setTextProperty(property);
            }

            if (annotations.annotationForProperty(
                    property, ArooaComponent.class.getName()) != null) {
                accumulator.setComponentProperty(property);
            }

            ArooaAnnotation namedAnnotation = annotations.annotationForProperty(
                    property, Named.class.getName());

            if (namedAnnotation != null) {
                Named named = namedAnnotation.realAnnotation(Named.class);
                if (named == null) {
                    throw new IllegalArgumentException(
                            "NAMED must be a real annotation.");
                }

                accumulator.setFlavour(property, named.value());

            }

            if (annotations.annotationForProperty(
                    property, Inject.class.getName()) != null) {
                accumulator.setAuto(property);
            }
        }
    }
}
