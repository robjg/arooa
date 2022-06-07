package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Encapsulate all the supported methods for providing an
 * {@link ArooaBeanDescriptor}.
 *
 * @author rob
 */
public class SupportedBeanDescriptorProvider implements BeanDescriptorProvider {

    private final DefaultBeanDescriptorProvider defaultBeanDescriptorProvider
            = new DefaultBeanDescriptorProvider();

    private final BeanDefinitionContributor beanDefinitionContributor
            = new BeanDefinitionContributor();

    private final AnnotatedBeanDescriptorContributor annotatedBeanDescriptorContributor
            = new AnnotatedBeanDescriptorContributor();

    private final BeanDefinition beanDefinition;

    private SupportedBeanDescriptorProvider(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public static BeanDescriptorProvider withBeanDefinition(BeanDefinition beanDefinition) {
        return new SupportedBeanDescriptorProvider(beanDefinition);
    }

    public static BeanDescriptorProvider withNoBeanDefinition() {
        return new SupportedBeanDescriptorProvider(null);
    }

    @Override
    public ArooaBeanDescriptor getBeanDescriptor(ArooaClass arooaClass,
                                                    PropertyAccessor accessor) {


        ArooaAnnotationsHelper annotationsHelper = new ArooaAnnotationsHelper(arooaClass);

        if (beanDefinition != null) {
            beanDefinition.toPropertyDefinitions()
                    .forEach(annotationsHelper::addPropertyDefinition);
            beanDefinition.toAnnotationDefinitions()
                    .forEach(annotationsHelper::addAnnotationDefinition);
        }

        BeanDescriptorBuilder descriptorBuilder =
                new BeanDescriptorBuilder(arooaClass);

        defaultBeanDescriptorProvider.findConfiguredHow(accessor, descriptorBuilder);

        annotatedBeanDescriptorContributor.makeContribution(annotationsHelper, descriptorBuilder);

        if (beanDefinition != null) {
            beanDefinitionContributor.makeContribution(beanDefinition, descriptorBuilder);
        }

        descriptorBuilder.setArooaAnnotations(annotationsHelper.toArooaAnnotations());

        ArooaBeanDescriptor beanDescriptor = descriptorBuilder.build();

        ArooaBeanDescriptor classDescriptor = new ClassBeanDescriptorProvider()
                .getBeanDescriptor(arooaClass, accessor);

        if (classDescriptor != null) {
            return new LinkedBeanDescriptor(
                    classDescriptor, beanDescriptor);
        }
        else {
            return beanDescriptor;
        }
    }
}
