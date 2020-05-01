package org.oddjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Calculate where the Oddjob code and application is.
 * <p/>
 * Checks the properties {@code oddjob.src} and {@code oddjob.app}
 *
 * @author rob
 *
 */
public class OddjobSrc {

	private static final Logger logger = LoggerFactory.getLogger(OddjobSrc.class);

	private static final OddjobSrc INSTANCE = new OddjobSrc();

	private final File oddjobSrc;

	private final Path oddjobApp;

	private final Path appJar;

	public OddjobSrc() {

		String oddjobSrcFromProperty = System.getProperty("oddjob.src");

		if (oddjobSrcFromProperty == null) {

			Path pwd = OurDirs.basePath();
			if ("oddjob".equals(pwd.getFileName().toString())) {
				logger.info("This appears to be the oddjob project.");
				oddjobSrc = pwd.toFile();
			}
			else {
				try {
					oddjobSrc = pwd.resolve("../oddjob").toRealPath().toFile();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
				logger.info("Guessed oddjob.src to be a parallel directory, {}.",
						oddjobSrc);
			}
		}
		else {

			try {
				oddjobSrc = new File(oddjobSrcFromProperty).getCanonicalFile();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

			logger.info("oddjob.src=" + oddjobSrc.toString());
		}

		if (!oddjobSrc.exists()) {
			throw new IllegalStateException(oddjobSrc + " does not exist.");
		}

		String appDirFromProperty = System.getProperty("oddjob.app");

		Path oddjobApp;

		if (appDirFromProperty == null) {

			oddjobApp  = OurDirs.basePath()
					.resolve("../run-oddjob")
					.resolve(OurDirs.buildType().getBuildDir())
					.resolve("oddjob");


			logger.info("Guessed oddjob.app to be {}", oddjobApp);
		}
		else {
			oddjobApp = Paths.get(appDirFromProperty);
			logger.info("oddjob.app={}", oddjobApp.toString());
		}

		if (!Files.exists(oddjobApp)) {
			throw new IllegalStateException(
					"Oddjob application has not been built: " + oddjobApp);
		}

		try {
			this.oddjobApp = oddjobApp.toRealPath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		this.appJar = this.oddjobApp.resolve("run-oddjob.jar");

		if (!Files.exists(appJar)) {
			throw new IllegalStateException(
					"Oddjob application jar has not been built: " + appJar);
		}

	}
	
	public File oddjobSrcBase() {
		return oddjobSrc;
	}

	public static Path oddjobSrc() {
		return INSTANCE.oddjobSrc.toPath();
	}

	public static Path oddjobApp()  {
		return INSTANCE.oddjobApp;
	}

	public static Path appJar()  {
		return INSTANCE.appJar;
	}
}
