package org.oddjob.arooa.utils;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

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

        assertThat(ClassUtils.cast(int.class, new Integer(42)), Matchers.is(42));
        assertThat(ClassUtils.cast(String.class, "Apple"), Matchers.is("Apple"));
        assertThat(ClassUtils.cast(void.class, null), Matchers.nullValue());
    }
}
