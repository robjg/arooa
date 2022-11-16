package org.oddjob.arooa.utils;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ClassUtilsGenericsTest {

    String justString() {
        return null;
    }

    List<String> listOfString() {
        return null;
    }


    @Test
    public void testRawTypeForGeneric() throws NoSuchMethodException {

        Type t1 = ClassUtilsGenericsTest.class.getDeclaredMethod("justString").getGenericReturnType();

        assertThat(ClassUtils.rawType(t1), is(String.class));

        Type t2 = ClassUtilsGenericsTest.class.getDeclaredMethod("listOfString").getGenericReturnType();

        assertThat(ClassUtils.rawType(t2), is(List.class));
    }

    void setListOfString(List<String> l) {}

    @SuppressWarnings("rawtypes")
    void setListRaw(List l) {}

    @Test
    public void testComponentType() throws NoSuchMethodException {

        assertThat(ClassUtils.getComponentTypeOfParameter(
                        ClassUtilsGenericsTest.class.getDeclaredMethod("setListOfString", List.class), 0),
                is(String.class));

        assertThat(ClassUtils.getComponentTypeOfParameter(
                        ClassUtilsGenericsTest.class.getDeclaredMethod("setListRaw", List.class), 0),
                nullValue());
    }
}
