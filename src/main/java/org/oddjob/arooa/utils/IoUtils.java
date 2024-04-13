package org.oddjob.arooa.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.LongConsumer;

/**
 * IO utilities. The sort of things that are in Apache Commons or Guava but
 * we don't want the dependency.
 * <p>
 * As with Apache Commons IO these methods do not close or flush any streams.
 */
public class IoUtils {

    public static final int BUFFER_SIZE = 8192;

    public static long copy(InputStream in, OutputStream out) throws IOException {

        return copy(in, out, null);
    }


    public static long copy(InputStream in, OutputStream out, LongConsumer progress) throws IOException {

        final LongConsumer notNullProgress = Optional.ofNullable(progress).orElse(value -> {
        });

        final byte[] buf = new byte[BUFFER_SIZE];
        long count = 0;

        int read;
        while ((read = in.read(buf)) > 0) {
            out.write(buf, 0, read);
            notNullProgress.accept(count += read);
        }
        return read;
    }

    public static long write(String string, OutputStream out) throws IOException {

        return copy(new ByteArrayInputStream(string.getBytes()), out);
    }

    /**
     * Read an Input Stream in as a String.
     *
     * @param in The InputStream.
     * @return A string.
     * @throws IOException If the stream can't be read.
     */
    public static String read(InputStream in) throws IOException {

        return new String(new BufferedInputStream(in).readAllBytes(), StandardCharsets.UTF_8);
    }

}
