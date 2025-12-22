package org.oddjob.arooa.convert.jokers;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.*;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayConversionsTest {

    @Test
    public void testArrayConvert() throws Exception {

        DefaultConversionRegistry reg = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(reg);

        DefaultConverter test = new DefaultConverter(reg);

        String[] sa = {"3", "9"};
        ConversionPath<String[], ?> result = test.findConversion(String[].class, int[].class);

        assertEquals(String[].class, result.getFromClass());
        assertEquals(int[].class, result.getToClass());

        assertEquals(2, result.length());

        int[] resultArray = (int[]) result.convert(sa, test);

        assertEquals(2, resultArray.length);

        assertEquals(3, resultArray[0]);
        assertEquals(9, resultArray[1]);
    }

    @Test
    public void testConvertSingleToArray() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionPath<Integer, Integer[]> path = registry.findConversion(
                Integer.class, Integer[].class);

        assertEquals("Integer-Number-Object-Integer[]", path.toString());

        Object[] result = path.convert(42, null);

        assertEquals(1, result.length);
        assertEquals(42, result[0]);

    }

    /**
     * 42 gets converted to true?
     */
    @Test
    public void testABigFatBugThatNeedFixing() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionPath<Integer, String[]> path = registry.findConversion(
                Integer.class, String[].class);

        assertEquals("Integer-Number-Object-String[]", path.toString());

        String[] result = path.convert(42, null);

        assertEquals(1, result.length);
        assertEquals("true", result[0]);

    }

    @Test
    public void testFilesToStrings() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionPath<File[], String[]> path = registry.findConversion(
                File[].class, String[].class);

        assertEquals("File[]-Object-String[]", path.toString());

        Object[] result = path.convert(
                new File[]{new File("a.txt"), new File("b.txt")}, null);

        assertEquals(2, result.length);
        assertEquals("a.txt", result[0]);
        assertEquals("b.txt", result[1]);
    }

    @Test
    void intsToStrings() throws ConversionFailedException {

        DefaultConverter converter = new DefaultConverter();

        ConversionPath<int[], String[]> path = converter.findConversion(
                int[].class, String[].class);

        assertEquals("int[]-Object-String[]", path.toString());

        String[] result = path.convert(new int[] { 1, 2, 3}, converter);

        assertThat(result, is(new String[] { "1", "2", "3" }));
    }

    @Test
    void StringsToInts() throws ConversionFailedException {

        DefaultConverter converter = new DefaultConverter();

        ConversionPath<String[], int[]> path = converter.findConversion(
                String[].class, int[].class);

        assertEquals("String[]-Object-int[]", path.toString());

        int[] result = path.convert(new String[] { "1", "2", "3" }, converter);

        assertThat(result, is(new int[] { 1, 2, 3}));
    }
}
