package org.oddjob.arooa.convert.convertlets;

import org.junit.Before;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;

import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;

public class FileConvertletsTest extends Assert {

    private File workDir;

    @Before
    public void setUp() throws IOException {

        workDir = OurDirs.workPathDir(getClass().getSimpleName(), true)
                .toFile();
    }

    @Test
    public void testFile2String() throws NoConversionAvailableException, ConversionFailedException {
        DefaultConverter test = new DefaultConverter();

        File from = new File("test.txt");
        String result = test.convert(from, String.class);

        assertEquals("test.txt", result);
    }

    @Test
    public void testString2File() throws NoConversionAvailableException, ConversionFailedException {
        DefaultConverter test = new DefaultConverter();

        String from = "test.txt";
        File result = test.convert(from, File.class);

        assertEquals(new File("test.txt"), result);
    }

    @Test
    public void testFile2InputStream() throws ConvertletException, IOException, NoConversionAvailableException, ConversionFailedException {
        DefaultConverter converter = new DefaultConverter();

        File file = new File(workDir, "FileTypeTest.dat");
        file.delete();
        file.createNewFile();

        InputStream result = converter.convert(file, InputStream.class);

        assertEquals(-1, result.read());

        result.close();
    }

    @Test
    public void testFile2OutputStream() throws IOException, NoConversionAvailableException, ConversionFailedException {
        DefaultConverter converter = new DefaultConverter();

        File file = new File(workDir, "FileTypeTest.dat");
        file.createNewFile();

        OutputStream result = converter.convert(file, OutputStream.class);

        result.write(65);

        result.close();
    }

    @Test
    public void testFile2URL() throws NoConversionAvailableException, ConversionFailedException, MalformedURLException {
        DefaultConverter converter = new DefaultConverter();

        File file = new File("test.txt");

        URL result = converter.convert(file, URL.class);

        assertEquals(file.toURI().toURL(), result);
    }

    @Test
    public void testFilesToString() throws NoConversionAvailableException, ConversionFailedException {
        DefaultConverter converter = new DefaultConverter();

        File[] files = new File[]{
                new File("A.txt"), new File("B.txt")
        };

        String result = converter.convert(files, String.class);

        assertEquals("A.txt" + File.pathSeparator + "B.txt",
                result);
    }

    @Test
    public void testStringToFiles() throws NoConversionAvailableException, ConversionFailedException {
        DefaultConverter converter = new DefaultConverter();

        String path = "A.txt" + File.pathSeparator + "B.txt";
        File[] result = converter.convert(path, File[].class);

        assertEquals(2, result.length);
        assertEquals(new File("A.txt"), result[0]);
        assertEquals(new File("B.txt"), result[1]);
    }
}
