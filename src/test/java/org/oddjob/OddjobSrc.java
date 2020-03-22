package org.oddjob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculate where the Application code is.
 * <p/>
 * Note that {@code oddjob.src} is deprecated in favour of {@code oddjob.app}
 *
 * @author rob
 *
 */
public class OddjobSrc {

	private static final Logger logger = LoggerFactory.getLogger(OddjobSrc.class);
	
	private final File oddjobSrc;	

	private final File oddjobApp;

	public OddjobSrc() throws IOException {
		String oddjobSrcFromProperty = System.getProperty("oddjob.src");
		if (oddjobSrcFromProperty != null) {
			oddjobSrc = new File(oddjobSrcFromProperty).getCanonicalFile();
			logger.info("oddjob.src=" + oddjobSrc.toString());
		}
		else {
			File pwd = new File(".").getCanonicalFile();
			if ("oddjob".equals(pwd.getName())) {
				logger.info("This appears to be the oddjob project.");
				oddjobSrc = new File(".");
			}
			else {
				logger.info("Guess oddjob.src to be a parallel directory.");
				oddjobSrc = new OurDirs().relative("../oddjob").getCanonicalFile();
			}
		}

		String appDirFromProperty = System.getProperty("oddjob.app");
		if (appDirFromProperty == null) {
			OurDirs whereWeAre = new OurDirs();

			File buildDir = whereWeAre.relative("../run-oddjob/target");
			if (!buildDir.exists()) {
				buildDir = whereWeAre.relative("../run-oddjob/build");
			}
			if (!buildDir.exists()) {
				throw new FileNotFoundException("No maven or ant build directory found from "
						+ whereWeAre.base());
			}

			oddjobApp = new File(buildDir, "oddjob").getCanonicalFile();
			logger.info("Guessed oddjob.app to be {}", oddjobApp);
		}
		else {
			oddjobApp = new File(appDirFromProperty).getCanonicalFile();
			logger.info("oddjob.app={}", oddjobApp.toString());
		}

		if (!oddjobSrc.exists()) {
			throw new FileNotFoundException(oddjobSrc + " does not exist.");
		}
	}
	
	public File oddjobSrcBase() {
		return oddjobSrc;
	}

	public File oddjobApp() throws FileNotFoundException {
		if (!oddjobApp.exists()) {
			throw new FileNotFoundException("Oddjob App " + oddjobApp + " does not exist.");
		}
		return oddjobApp;
	}
}
