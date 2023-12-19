package org.oddjob.arooa.utils;

import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FileUtilsTest {

    @Test
    void deleteRecursively() throws IOException {

        Path workDir = OurDirs.workPathDir(FileUtilsTest.class, "deleteRecursive");

        Path dirB = Files.createDirectories(workDir.resolve("a/b"));
        Path dirA = dirB.getParent();

        Path someFile = dirB.resolve("SomeFile.txt");

        Files.writeString(someFile, "Some File");

        FileUtils.deleteDirectory(dirB.getParent());

        assertThat(Files.exists(dirA), is(false));
    }

}
