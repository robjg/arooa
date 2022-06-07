package org.oddjob.arooa.deploy;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.deploy.annotations.*;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AnnotatedBeanDescriptorTest {

    public static class OurInterceptor implements ParsingInterceptor {
        public ArooaContext intercept(ArooaContext suggestedContext) {
            return null;
        }
    }

    @ArooaInterceptor("org.oddjob.arooa.deploy.AnnotatedBeanDescriptorTest$OurInterceptor")
    public static class Bean {

        @ArooaComponent
        public void setMyComp(Object ignored) {
        }

        @ArooaText
        public void setMyText(Object ignored) {
        }

        @Inject
        @Named("red")
        @ArooaAttribute
        public void setMyAttribute(Object ignored) {
        }

        @Inject
        @ArooaElement
        public void setMyValue(String ignored) {
        }

    }

    @Test
    public void testDescriptorBuiltFromClassAnnotationsOnly() {

        AnnotatedBeanDescriptorContributor test =
                new AnnotatedBeanDescriptorContributor();

        BeanDescriptorBuilder builder = new BeanDescriptorBuilder(new SimpleArooaClass(Bean.class));

        ArooaAnnotationsHelper arooaAnnotations = new ArooaAnnotationsHelper(builder.getClassIdentifier());

        test.makeContribution(arooaAnnotations, builder);

        ArooaBeanDescriptor beanDescriptor =
                builder.build();

        assertThat(beanDescriptor, notNullValue());

        ParsingInterceptor interceptor =
                beanDescriptor.getParsingInterceptor();

        assertThat(interceptor.getClass(), Matchers.is(OurInterceptor.class));

        assertThat(beanDescriptor.getTextProperty(), is("myText"));

        assertThat(beanDescriptor.getComponentProperty(), is("myComp"));

        assertThat(beanDescriptor.getConfiguredHow("myAttribute"),
                is(ConfiguredHow.ATTRIBUTE));

        assertThat(beanDescriptor.getConfiguredHow("myValue"),
                is(ConfiguredHow.ELEMENT));

        assertThat(beanDescriptor.isAuto("myAttribute"), is(true));

        assertThat(beanDescriptor.isAuto("myValue"), is(true));

        assertThat(beanDescriptor.getFlavour("myAttribute"), is("red"));

        assertThat(beanDescriptor.getFlavour("myValue"), nullValue());
    }

    public static class Bean2 {

        public void setMyComp(Object ignored) {
        }

        public void setMyText(String ignored) {
        }

        public void setMyAttribute(Object ignored) {
        }

        public void setMyValue(String ignored) {
        }

    }

    @Test
    public void testDescriptorBuiltFromPropertyDefinitionsOnly() {

        AnnotatedBeanDescriptorContributor test =
                new AnnotatedBeanDescriptorContributor();

        BeanDescriptorBuilder builder = new BeanDescriptorBuilder(new SimpleArooaClass(Bean2.class));

        ArooaAnnotationsHelper arooaAnnotations = new ArooaAnnotationsHelper(builder.getClassIdentifier());

        PropertyDefinitionBean propDef1 = new PropertyDefinitionBean();
        propDef1.setName("myComp");
        propDef1.setAnnotation(ArooaComponent.class.getName());

        PropertyDefinitionBean propDef2 = new PropertyDefinitionBean();
        propDef2.setName("myText");
        propDef2.setAnnotation(ArooaText.class.getName());

        PropertyDefinitionBean propDef3 = new PropertyDefinitionBean();
        propDef3.setName("myAttribute");
        propDef3.setAnnotation(ArooaAttribute.class.getName());

        PropertyDefinitionBean propDef4 = new PropertyDefinitionBean();
        propDef4.setName("myValue");
        propDef4.setAnnotation(ArooaElement.class.getName());

        arooaAnnotations.addPropertyDefinition(propDef1);
        arooaAnnotations.addPropertyDefinition(propDef2);
        arooaAnnotations.addPropertyDefinition(propDef3);
        arooaAnnotations.addPropertyDefinition(propDef4);

        test.makeContribution(arooaAnnotations, builder);

        ArooaBeanDescriptor beanDescriptor =
                builder.build();

        assertThat(beanDescriptor, notNullValue());

        assertThat(beanDescriptor.getTextProperty(), is("myText"));

        assertThat(beanDescriptor.getComponentProperty(), is("myComp"));

        assertThat(beanDescriptor.getConfiguredHow("myAttribute"),
                is(ConfiguredHow.ATTRIBUTE));

        assertThat(beanDescriptor.getConfiguredHow("myValue"),
                is(ConfiguredHow.ELEMENT));
    }

    @Test
    public void testDescriptorBuiltFromSyntheticAnnotationsOnly() {

        AnnotatedBeanDescriptorContributor test =
                new AnnotatedBeanDescriptorContributor();

        BeanDescriptorBuilder builder = new BeanDescriptorBuilder(new SimpleArooaClass(Bean2.class));

        ArooaAnnotationsHelper arooaAnnotations = new ArooaAnnotationsHelper(builder.getClassIdentifier());

        AnnotationDefinitionBean annotationDef1 = new AnnotationDefinitionBean();
        annotationDef1.setMethod("setMyComp");
        annotationDef1.setParameterTypes(Object.class.getName());
        annotationDef1.setName(ArooaComponent.class.getName());

        AnnotationDefinitionBean annotationDef2 = new AnnotationDefinitionBean();
        annotationDef2.setMethod("setMyText");
        annotationDef2.setParameterTypes(String.class.getName());
        annotationDef2.setName(ArooaText.class.getName());

        AnnotationDefinitionBean annotationDef3 = new AnnotationDefinitionBean();
        annotationDef3.setMethod("setMyAttribute");
        annotationDef3.setParameterTypes(Object.class.getName());
        annotationDef3.setName(ArooaAttribute.class.getName());

        AnnotationDefinitionBean annotationDef4 = new AnnotationDefinitionBean();
        annotationDef4.setMethod("setMyValue");
        annotationDef4.setParameterTypes(String.class.getName());
        annotationDef4.setName(ArooaElement.class.getName());

        arooaAnnotations.addAnnotationDefinition(annotationDef1);
        arooaAnnotations.addAnnotationDefinition(annotationDef2);
        arooaAnnotations.addAnnotationDefinition(annotationDef3);
        arooaAnnotations.addAnnotationDefinition(annotationDef4);

        test.makeContribution(arooaAnnotations, builder);

        ArooaBeanDescriptor beanDescriptor =
                builder.build();

        assertThat(beanDescriptor, notNullValue());

        assertThat(beanDescriptor.getTextProperty(), is("myText"));

        assertThat(beanDescriptor.getComponentProperty(), is("myComp"));

        assertThat(beanDescriptor.getConfiguredHow("myAttribute"),
                is(ConfiguredHow.ATTRIBUTE));

        assertThat(beanDescriptor.getConfiguredHow("myValue"),
                is(ConfiguredHow.ELEMENT));
    }

    public static class ConfiguredInDescriptor {

        public void setMyComp(Object someComp) {
        }

        public void setKeys(String[] keys) {
        }

        public void setFoo(String foo) {
        }
    }

    @Test
    public void whenDescriptorConfiguredWithSyntheticAnnotations_thenCorrectHowUsed() {

        URL url = Objects.requireNonNull(
                getClass().getResource("ArooaAnnotationsForConfiguration.xml"));

        URLDescriptorFactory df = new URLDescriptorFactory(url);

        ArooaDescriptor ad = df.createDescriptor(getClass().getClassLoader());

        ArooaBeanDescriptor beanDescriptor = ad.getBeanDescriptor(new SimpleArooaClass(ConfiguredInDescriptor.class),
                new BeanUtilsPropertyAccessor());

        ArooaAnnotations annotations = beanDescriptor.getAnnotations();

        assertThat(beanDescriptor.getConfiguredHow("keys"), is(ConfiguredHow.ATTRIBUTE));

        assertThat(beanDescriptor.getComponentProperty(), is("myComp"));
    }
}
