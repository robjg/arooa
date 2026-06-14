package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.deploy.annotations.*;
import org.oddjob.arooa.utils.ClassUtils;

import javax.inject.Inject;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

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
            if (!interceptor.isEmpty()) {
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

            // Finds any Qualifier annotations. Only finds the last.
            // No handling of more than one qualifier yet.
            List<Annotation> propertyAnnotations = annotations.annotationsFor(property);
            for (Annotation propertyAnnotation : propertyAnnotations) {

                Arrays.stream(propertyAnnotation
                                .annotationType().getAnnotations())
                        .filter(an -> an.annotationType() == Qualifier.class)
                        .forEach(an -> accumulator.setQualifier(property, propertyAnnotation));
            }

            if (annotations.annotationForProperty(
                    property, Inject.class.getName()) != null) {
                accumulator.setAuto(property);
            }
        }
    }
}
