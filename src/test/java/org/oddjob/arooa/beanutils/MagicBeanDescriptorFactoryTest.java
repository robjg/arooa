package org.oddjob.arooa.beanutils;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptorFactory;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.utils.Pair;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MagicBeanDescriptorFactoryTest {

    public static class Stuff {

        Object person;

        public Object getPerson() {
            return person;
        }

        public void setPerson(Object person) {
            this.person = person;
        }
    }

    @Test
    public void testSimpleDefinition() throws ArooaParseException, ArooaPropertyException, ArooaConversionException, URISyntaxException {

        String definition =
                "<arooa:magic-beans xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'" +
                        "   namespace='oddjob:magic' prefix='magic'>" +
                        " <definitions>" +
                        "  <is element='person'>" +
                        "   <properties>" +
                        "    <is name='name' type='java.lang.String'/>" +
                        "    <is name='age' type='java.lang.Integer'/>" +
                        "   </properties>" +
                        "  </is>" +
                        " </definitions>" +
                        "</arooa:magic-beans>";


        String xml =
                "<test id='t' xmlns:magic='oddjob:magic'>" +
                        " <person>" +
                        "  <magic:person name='John' age='22'/>" +
                        " </person>" +
                        "</test>";


        StandardFragmentParser parser =
                new StandardFragmentParser(
                        new ArooaDescriptorDescriptorFactory().createDescriptor(
                                getClass().getClassLoader()));

        parser.parse(new XMLConfiguration("XML", definition));

        MagicBeanDescriptorFactory mbdf =
                (MagicBeanDescriptorFactory) parser.getRoot();

        parser.getSession().getComponentPool().configure(mbdf);

        ArooaDescriptor descriptor = mbdf.createDescriptor(
                getClass().getClassLoader());

        // Check element support
        InstantiationContext instantiationContext =
                new InstantiationContext(
                        ArooaType.VALUE,
                        new SimpleArooaClass(Object.class));

        ArooaElement[] elements = descriptor.getElementMappings(
        ).elementsFor(instantiationContext);

        assertThat(elements, is(new ArooaElement[] { new ArooaElement(
                new URI("oddjob:magic"), "person")}));

        instantiationContext =
                new InstantiationContext(
                        ArooaType.VALUE,
                        new SimpleArooaClass(ArooaValue.class),
                        null,
                        new DefaultConverter());

        elements = descriptor.getElementMappings().elementsFor(instantiationContext);

        assertThat(elements, is(new ArooaElement[] { new ArooaElement(
                new URI("oddjob:magic"), "person") }));

        // Parse
        Stuff stuff = new Stuff();

        StandardArooaParser parser2 = new StandardArooaParser(
                stuff, descriptor);

        parser2.parse(new XMLConfiguration("XML", xml));

        ArooaSession session = parser2.getSession();

        session.getComponentPool().configure(stuff);

        int age = session.getBeanRegistry().lookup(
                "t.person.age", int.class);
        String name = session.getBeanRegistry().lookup(
                "t.person.name", String.class);

        assertThat(age, is(22));
        assertThat(name, is("John"));
    }

    @Test
    public void testWithPropertyAccessor() {

        MagicBeanDefinition def = new MagicBeanDefinition();
        def.setElement("OurMagicBean");

        MagicBeanDescriptorProperty prop1 = new MagicBeanDescriptorProperty();
        prop1.setName("fruit");
        prop1.setType(String.class.getName());

        MagicBeanDescriptorProperty prop2 = new MagicBeanDescriptorProperty();
        prop2.setName("quantity");
        prop2.setType(Integer.class.getName());

        def.setProperties(0, prop1);
        def.setProperties(1, prop2);

        Pair<ArooaClass, ArooaBeanDescriptor> magicPair = def.createMagic(this.getClass().getClassLoader());

        ArooaClass cl = magicPair.getLeft();

        Object test = cl.newInstance();

        PropertyAccessor accessor =
                new BeanUtilsPropertyAccessor().accessorWithConversions(new DefaultConverter());

        accessor.setProperty(test, "fruit", "Apple");
        accessor.setProperty(test, "quantity", "5");

        assertThat(accessor.getProperty(test, "fruit"), is("Apple"));
        assertThat(accessor.getProperty(test, "quantity"), is(5));

    }

    @Test
    public void testClassIdentifier() {

        MagicBeanDefinition def = new MagicBeanDefinition();
        def.setElement("OurMagicBean");

        MagicBeanDescriptorProperty prop1 = new MagicBeanDescriptorProperty();
        prop1.setName("fruit");
        prop1.setType(String.class.getName());

        MagicBeanDescriptorProperty prop2 = new MagicBeanDescriptorProperty();
        prop2.setName("quantity");
        prop2.setType(Integer.class.getName());

        def.setProperties(0, prop1);
        def.setProperties(1, prop2);

        Pair<ArooaClass, ArooaBeanDescriptor> magicPair = def.createMagic(this.getClass().getClassLoader());

        ArooaClass cl = magicPair.getLeft();

        Object test = cl.newInstance();

        PropertyAccessor accessor = new BeanUtilsPropertyAccessor();

        ArooaClass identifier = accessor.getClassName(test);

        BeanOverview overview = identifier.getBeanOverview(accessor);

        assertThat(overview, Matchers.instanceOf(DynaBeanOverview.class));

        assertThat("Fruit readable", overview.hasReadableProperty("fruit"));
        assertThat("Fruit writeable", overview.hasWriteableProperty("fruit"));

        assertThat("Quantity readable", overview.hasReadableProperty("quantity"));
        assertThat("Quantity writeable", overview.hasWriteableProperty("quantity"));

        assertThat("Stuff not readable", !overview.hasReadableProperty("stuff"));
        assertThat("Stuff not writeable", !overview.hasWriteableProperty("stuff"));
    }
}
