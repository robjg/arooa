package org.oddjob.arooa.utils;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class ClassUtilsTest extends Assert {

    @Test
    public void testClassFor() throws ClassNotFoundException {

        assertEquals(String.class,
                ClassUtils.classFor(
                        "java.lang.String", getClass().getClassLoader()));

        assertEquals(int.class,
                ClassUtils.classFor(
                        int.class.getName(), getClass().getClassLoader()));

        assertEquals(int[].class,
                ClassUtils.classFor(
                        int[].class.getName(), getClass().getClassLoader()));

        assertEquals(int[][].class,
                ClassUtils.classFor(
                        int[][].class.getName(), getClass().getClassLoader()));
    }

    // To visually check the error message.
    @Test
    public void testError() {

        try {
            ClassUtils.classFor("A.Flying.Pig", getClass().getClassLoader());
            fail("Should fail.");
        } catch (ClassNotFoundException e) {
            // expected.
        }
    }


    @Test
    public void testClassesFor() throws ClassNotFoundException {

    	Class<?>[] results = ClassUtils.classesFor(
    			new String[] { "int", "long", "java.lang.Void", "void"},
				getClass().getClassLoader());


    	assertThat(results, Matchers.arrayContaining(
    			int.class, long.class, Void.class, void.class));
	}

	@Test
	public void testCast() {

        assertThat(ClassUtils.cast(int.class, new Integer(42)), is(42));
        assertThat(ClassUtils.cast(String.class, "Apple"), is("Apple"));
        assertThat(ClassUtils.cast(void.class, null), Matchers.nullValue());
    }


    // How to get a simple name from different class types.
    @Test
    public void testSimpleName() {

        Object foo = new Object() {};

        // The assumptions
        assertThat(TopLevelClass.class.getEnclosingClass(), Matchers.nullValue());
        assertThat(Shapes.SQUARE.getClass().getSimpleName(), is("Shapes"));
        assertThat(Colours.RED.getClass().getSimpleName(), is(""));
        assertThat(Colours.RED.getClass().getEnclosingClass().getSimpleName(), is("Colours"));
        assertThat(foo.getClass().getSimpleName(), is(""));
        assertThat(foo.getClass().getEnclosingClass().getSimpleName(), is("ClassUtilsTest"));

        assertThat(ClassUtils.getSimpleName(Shapes.SQUARE.getClass()), is("Shapes"));
        assertThat(ClassUtils.getSimpleName(Colours.RED.getClass()), is("Colours"));
        assertThat(ClassUtils.getSimpleName(foo.getClass()), is("ClassUtilsTest"));
    }
}

enum Shapes {

    SQUARE,
    CIRCLE;
}

enum Colours {

    RED {
        @Override
        String getColour() {
            return "Red";
        }
    },
    GREEN {
        @Override
        String getColour() {
            return "Green";
        }
    };

    abstract String getColour();
}


class TopLevelClass {

}