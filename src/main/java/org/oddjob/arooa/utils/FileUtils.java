package org.oddjob.arooa.utils;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * File utilities. The sort of things that are in Apache Commons or Guava, but we don't want the dependency.
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

    /**
     * Deletes a directory including subdirectory. Unlike the commons version it doesn't support deleting
     * read only files and following links.
     *
     * @param directory The directory.
     * @throws IOException If something goes wrong.
     */
    public static void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
