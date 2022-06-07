package org.oddjob.arooa.deploy;

import org.junit.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PropertyDefinitionBeanTest {

    @Test
    public void testXMLConfiguration() throws ArooaParseException {


        ArooaSession session = new StandardArooaSession(
                new ArooaDescriptorDescriptor());

        StandardFragmentParser parser = new StandardFragmentParser(
                session);

        ArooaConfiguration config =
                new XMLConfiguration("TEST",
                        "<arooa:property xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'" +
                                " name='myProp' type='ATTRIBUTE' " +
                                " auto='true' flavour='red'/>");

        parser.parse(config);

        PropertyDefinitionBean test = (PropertyDefinitionBean) parser.getRoot();

        assertThat(test.getConfiguredHow(), is(ConfiguredHow.ATTRIBUTE));
        assertThat(test.getFlavour(), is("red"));

        assertThat(test.getAuto(), is(true));
    }

    @Test
    public void testSettingAnnotationNoLongerSetsType() {

        PropertyDefinitionBean test1 = new PropertyDefinitionBean();
        test1.setAnnotation(ArooaAttribute.class.getName());
        assertThat(test1.getAnnotation(), is(ArooaAttribute.class.getName()));
        assertThat(test1.getType(), nullValue());
        assertThat(test1.getConfiguredHow(), nullValue());
    }
}
