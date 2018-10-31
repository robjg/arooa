package org.oddjob;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to work out relative directories, when running tests individually
 * from eclipse or from ant.
 * <p>
 * When running from ant the property basedir should be set which is the
 * project root.
 * 
 * @author rob
 */
public class OurDirs {
	private static final Logger logger = LoggerFactory.getLogger(OurDirs.class);

	private static final OurDirs INSTANCE = new OurDirs();

	private final File base;

	private final Path buildDirPath;

	public OurDirs() {
		String baseDir = System.getProperty("basedir");
		if (baseDir != null) {
			base = new File(baseDir);
		}
		else {
			base = new File(".");
		}

		logger.info("base is " + base.getAbsolutePath());

		File build = new File(base, "build.xml");
		if (!build.exists()) {
			throw new IllegalStateException("Can't find " +
					build + ", where you running this from?");
		}

		String buildDir = System.getProperty("project.build.directory");
		if (buildDir == null) {
			this.buildDirPath = base.toPath().resolve("target");
		}
		else {
			this.buildDirPath = Paths.get(buildDir);
		}

        try {
            mkDirs(this.buildDirPath, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public File base() {
		try {
			return base.getCanonicalFile();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public File relative(String name) {
		try {
			return new File(base, name).getCanonicalFile();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Path basePath() {
		return INSTANCE.base.toPath();
	}

	public static Path relativePath(String other) {
		return basePath().resolve(other);
	}

	public static Path buildDirPath() {
		return INSTANCE.buildDirPath;
	}

	public static Path workDirPath() throws IOException {

        return mkDirs(buildDirPath().resolve("work"), false);
	}

    public static Path workPathDir(String other, boolean recreate) throws IOException {
        return mkDirs(workDirPath().resolve(other), recreate);
    }

    private static Path mkDirs(Path dir, boolean recreate) throws IOException {
        if (Files.exists(dir)) {
            if (recreate) {
                deleteDir(dir);
            }
            else {
                if (!Files.isDirectory(dir)) {
                    throw new IllegalArgumentException(dir + " is not a directory");
                }
                return dir;
            }
        }
        return Files.createDirectories(dir);
    }

    private static void deleteDir(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
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
