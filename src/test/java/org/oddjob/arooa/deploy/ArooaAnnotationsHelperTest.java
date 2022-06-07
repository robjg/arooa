package org.oddjob.arooa.deploy;

import org.junit.Test;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.Configured;
import org.oddjob.arooa.life.Destroy;
import org.oddjob.arooa.life.Initialised;
import org.oddjob.arooa.life.SimpleArooaClass;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ArooaAnnotationsHelperTest {

    public static class MyBase {

        @ArooaAttribute
        private String shape;

        @Destroy
        public void myDestroy() {
        }

        public void myInit() {
        }
    }

    public static class MyBean extends MyBase {

        @ArooaHidden
        private String colour;

        @ArooaComponent
        public void setFruit() {
        }

        @Configured
        public void doStuff() {
        }
    }


    @Test
    public void testMethodAnnotations() {

        ArooaAnnotationsHelper test = new ArooaAnnotationsHelper(
                new SimpleArooaClass(MyBean.class));


        AnnotationDefinitionBean def = new AnnotationDefinitionBean();
        def.setMethod("missing");
        def.setName("org.oddjob.test.Anything");

        try {
            test.addAnnotationDefinition(def);
        } catch (RuntimeException e) {
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
        }

        def.setMethod("doStuff");

        test.addAnnotationDefinition(def);

        def.setMethod("myInit");
        def.setName(Initialised.class.getName());

        test.addAnnotationDefinition(def);

        ArooaAnnotations arooaAnnotations = test.toArooaAnnotations();

        Method method = arooaAnnotations.methodFor(ArooaComponent.class.getName());

        assertThat(method.getName(), is("setFruit"));

        method = arooaAnnotations.methodFor(Configured.class.getName());

        assertThat(method.getName(), is("doStuff"));

        method = arooaAnnotations.methodFor("org.oddjob.test.Anything");

        assertThat(method.getName(), is("doStuff"));

        method = arooaAnnotations.methodFor(Destroy.class.getName());

        assertThat(method.getName(), is("myDestroy"));

        method = arooaAnnotations.methodFor(Initialised.class.getName());

        assertThat(method.getName(), is("myInit"));
    }

    @Test
    public void testPropertyAnnotations() {

        ArooaAnnotationsHelper test = new ArooaAnnotationsHelper(
                new SimpleArooaClass(MyBean.class));

        String[] properties = test.annotatedProperties();

        ArooaAnnotation[] annotations = test.annotationsForProperty("colour");

        assertThat(annotations.length, is(1));

        ArooaAnnotation annotation = test.annotationForProperty("colour",
                ArooaHidden.class.getName());

        assertThat(annotation.realAnnotation(ArooaHidden.class).annotationType(),
                is(ArooaHidden.class));

        annotation = test.annotationForProperty("fruit",
                ArooaComponent.class.getName());

        assertThat(annotation.realAnnotation(ArooaComponent.class).annotationType(),
                is(ArooaComponent.class));

        assertThat(test.annotationForProperty("colour", "org.oddjob.test.Anything"),
                nullValue());

        PropertyDefinitionBean definition = new PropertyDefinitionBean();
        definition.setName("colour");
        definition.setAnnotation("org.oddjob.test.Anything");

        test.addPropertyDefinition(definition);

        annotation = test.annotationForProperty("colour",
                "org.oddjob.test.Anything");

        assertThat(annotation.getName(), is("org.oddjob.test.Anything"));

        annotation = test.annotationForProperty("shape",
                ArooaAttribute.class.getName());

        assertThat(annotation.getName(), is(ArooaAttribute.class.getName()));
    }

    public static class AnotherBean {

        public void setSomeFoo() {

        }
    }

    @Test
    public void testMethodDefinitionSetsPropertyType() {

        ArooaAnnotationsHelper test = new ArooaAnnotationsHelper(
                new SimpleArooaClass(AnotherBean.class));

        AnnotationDefinitionBean annotationDefinitionBean = new AnnotationDefinitionBean();
        annotationDefinitionBean.setMethod("setSomeFoo");
        annotationDefinitionBean.setName(ArooaHidden.class.getName());

        test.addAnnotationDefinition(annotationDefinitionBean);

        ArooaAnnotation arooaAnnotation = test.annotationForProperty(
                "someFoo", ArooaHidden.class.getName());

        assertThat(arooaAnnotation, notNullValue());
    }
}
