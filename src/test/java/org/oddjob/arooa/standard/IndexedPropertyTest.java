package org.oddjob.arooa.standard;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class IndexedPropertyTest extends Assert {

    public static class SimpleAndIndexed {

        List<String> things = new ArrayList<>();

        @ArooaAttribute
        public void setThings(String[] things) {
            this.things.addAll(Arrays.asList(things));
        }

        public void setThings(int index, String thing) {
            throw new RuntimeException("Unexpected");
        }

        public String[] getStuff() {
            return new String[]{"apples", "oranges"};
        }
    }

    @Test
    public void testSettingAsAttributeFails() throws ArooaParseException {

        String xml =
                "<x id='x' things='${x.stuff}'/>";

        SimpleAndIndexed x = new SimpleAndIndexed();

        StandardArooaParser parser = new StandardArooaParser(x);

        parser.parse(new XMLConfiguration("TEST", xml));

        try {
            parser.getSession().getComponentPool().configure(x);
            fail("Is impossible to use the array setter as a simple property like this.");
        } catch (Exception e) {
            // expected.
        }
    }

    @Test
    public void testElementFails() {

        String xml =
                "<x>" +
                        " <things>" +
                        "  <value value='apples'/>" +
                        " </things>" +
                        "</x>";

        SimpleAndIndexed x = new SimpleAndIndexed();

        StandardArooaParser parser = new StandardArooaParser(x);

        try {
            parser.parse(new XMLConfiguration("TEST", xml));
            fail("Should fail.");
        } catch (ArooaParseException e) {
            MatcherAssert.assertThat(e.getMessage().endsWith("Property things is not configured as an element."),
                    is(true));
        }

    }

    public static class SimpleAndIndexed2 {

        List<String> things = new ArrayList<>();

        public void setThings(String[] things) {
            throw new RuntimeException("This should be ignored.");
        }

        public void setThings(int index, String thing) {
            if (thing == null) {
                things.remove(index);
            } else {
                things.add(index, thing);
            }
        }

        public String[] getStuff() {
            return new String[]{"apples", "oranges"};
        }
    }

    /**
     * Just check understanding of BeanUtils while were here...
     *
     * @throws ArooaParseException
     */
    @Test
    public void testSettingFromArrays() throws ArooaParseException {

        String xml =
                "<x id='x'>" +
                        " <things>" +
                        "  <value value='${x.stuff[0]}'/>" +
                        "  <value value='${x.stuff[1]}'/>" +
                        " </things>" +
                        "</x>";

        SimpleAndIndexed2 x = new SimpleAndIndexed2();

        StandardArooaParser parser = new StandardArooaParser(x);

        parser.parse(new XMLConfiguration("TEST", xml));

        parser.getSession().getComponentPool().configure(x);

        assertEquals(2, x.things.size());
    }

    @Test
    public void testConfigureConfigureDestroy() throws ArooaParseException {

        String xml =
                "<x>" +
                        " <things>" +
                        "  <value value='a'/>" +
                        " </things>" +
                        "</x>";

        SimpleAndIndexed2 x = new SimpleAndIndexed2();

        StandardArooaParser parser = new StandardArooaParser(x);

        parser.parse(new XMLConfiguration("TEST", xml));

        assertEquals(0, x.things.size());

        parser.getSession().getComponentPool().configure(x);

        assertEquals(1, x.things.size());

        parser.getSession().getComponentPool().configure(x);

        assertEquals(1, x.things.size());

        parser.getSession().getComponentPool().contextFor(x).getRuntime().destroy();

        // destroy no longer removes index value properties
        assertEquals(1, x.things.size());
    }
}
