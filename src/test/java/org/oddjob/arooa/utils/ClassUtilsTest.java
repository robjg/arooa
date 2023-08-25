package org.oddjob.arooa.utils;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ClassResolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClassUtilsTest {

    @Test
    public void jdkAssumptionThatPrimitivesNotResolved() {

        assertThrows(ClassNotFoundException.class, () -> Class.forName("int"));
    }

    @Test
    void testClassFor() throws ClassNotFoundException {

        ClassLoader classLoader = getClass().getClassLoader();

        assertThat(ClassUtils.classFor("java.lang.String", classLoader), is(String.class));

        assertThat(ClassUtils.classFor("[Ljava.lang.String;", classLoader), is(String[].class));

        assertThat(ClassUtils.classFor("[[Ljava.lang.String;", classLoader), is(String[][].class));

        assertThat(ClassUtils.classFor("int", classLoader), is(int.class));

        assertThat(ClassUtils.classFor("[I", classLoader), is(int[].class));

        assertThat(ClassUtils.classFor("[[I", classLoader), is(int[][].class));
    }

    // To visually check the error message.
    @Test
    void testError() {

        ClassNotFoundException exception = assertThrows(ClassNotFoundException.class,
                () -> ClassUtils.classFor("A.Flying.Pig", getClass().getClassLoader()));

        assertThat(exception.getMessage(), Matchers.containsString("A.Flying.Pig"));
    }

    @Test
    void classesForWithClassLoader() throws ClassNotFoundException {

    	Class<?>[] results = ClassUtils.classesFor(
    			new String[] { "int", "long", "java.lang.Void", "void"},
				getClass().getClassLoader());


    	assertThat(results, Matchers.arrayContaining(
    			int.class, long.class, Void.class, void.class));
	}

    @Test
    void classesForWithClassResolver() throws ClassNotFoundException {

        Class<?>[] results = ClassUtils.classesFor(
                new String[] { "int", "long", "java.lang.Void", "void"},
                ClassResolver.getDefaultClassResolver());

        assertThat(results, Matchers.arrayContaining(
                int.class, long.class, Void.class, void.class));
    }

	@Test
	void testCast() {

        assertThat(ClassUtils.cast(int.class, 42), is(42));
        assertThat(ClassUtils.cast(String.class, "Apple"), is("Apple"));
        assertThat(ClassUtils.cast(void.class, null), Matchers.nullValue());
    }


    // How to get a simple name from different class types.
    @Test
    void testSimpleName() {

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
    CIRCLE
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