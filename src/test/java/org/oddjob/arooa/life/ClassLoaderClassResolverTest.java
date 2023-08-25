package org.oddjob.arooa.life;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.OurDirs;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ClassLoaderClassResolverTest extends Assert {

    public static class Apple {

    }

    @Test
    public void testFindClass() {

        ClassLoaderClassResolver test = new ClassLoaderClassResolver(
                getClass().getClassLoader());

        assertEquals(Apple.class,
                test.findClass(Apple.class.getName()));
        assertEquals(String.class,
                test.findClass(String.class.getName()));

    }

    @Test
    public void testFindPrimitives() {

        ClassLoaderClassResolver test = new ClassLoaderClassResolver(
                getClass().getClassLoader());

        assertEquals(boolean.class, test.findClass(boolean.class.getName()));
        assertEquals(byte.class, test.findClass(byte.class.getName()));
        assertEquals(short.class, test.findClass(short.class.getName()));
        assertEquals(char.class, test.findClass(char.class.getName()));
        assertEquals(int.class, test.findClass(int.class.getName()));
        assertEquals(long.class, test.findClass(long.class.getName()));
        assertEquals(float.class, test.findClass(float.class.getName()));
        assertEquals(double.class, test.findClass(double.class.getName()));
    }

    @Test
    public void testFindClassClassLoader() throws MalformedURLException, URISyntaxException {

        Path classes = OurDirs.classesDir(getClass());

        URLClassLoader classLoader = new URLClassLoader(new URL[]{
                classes.toUri().toURL()
        }, null);

        ClassLoaderClassResolver test =
                new ClassLoaderClassResolver(classLoader);

        Class<?> cl = test.findClass(Apple.class.getName());

        assertNotNull("Class found with classloader " + test, cl);

        assertEquals(classLoader, cl.getClassLoader());
    }


    @Test
    public void testGetResource() {

        ClassLoaderClassResolver test = new ClassLoaderClassResolver(
                getClass().getClassLoader());

        URL result = test.getResource(
                "org/oddjob/arooa/life/ResourceToFind.properties");

        assertNotNull(result);
    }

    @Test
    public void testGetResources() {

        ClassLoaderClassResolver test = new ClassLoaderClassResolver(
                getClass().getClassLoader());

        URL[] result = test.getResources(
                "org/oddjob/arooa/life/ResourceToFind.properties");

        assertEquals(1, result.length);
    }

    /**
     * It appears the ClassLoader.loadClass won't find arrays but
     * Class.forName will.
     *
     * @throws ClassNotFoundException
     */
    @Test
    public void testFindArrayClass() throws ClassNotFoundException {

        String name = String[].class.getName();

        // works
        Class.forName(name, true, getClass().getClassLoader());

        try {
            getClass().getClassLoader().loadClass(name);
            fail("This throws an exception.");
        } catch (ClassNotFoundException e) {
            // expected.
        }

        ClassLoaderClassResolver resolver = new ClassLoaderClassResolver(getClass().getClassLoader());

        Class<?> result = resolver.findClass(name);

        assertSame(String[].class, result);
    }
}
