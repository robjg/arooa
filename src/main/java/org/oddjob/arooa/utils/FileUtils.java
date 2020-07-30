package org.oddjob.arooa.utils;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File utilities. The sort of things that are in Apache Commons or Guava but we don't want the dependency.
 */
public class FileUtils {

    public static String readToString(URL url) throws URISyntaxException, IOException {
        return readToString(url.toURI());
    }

    public static String readToString(URI uri) throws IOException {
        return readToString(Paths.get(uri));
    }

    public static String readToString(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }
}
