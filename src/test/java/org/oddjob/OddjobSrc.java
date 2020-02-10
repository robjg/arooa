package org.oddjob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculate where the Oddjob source code is.
 * 
 * @author rob
 *
 */
public class OddjobSrc {

	private static final Logger logger = LoggerFactory.getLogger(OddjobSrc.class);
	
	private final File oddjobSrc;	

	public OddjobSrc() throws IOException {
		String baseDir = System.getProperty("oddjob.src");
		if (baseDir != null) {
			oddjobSrc = new File(baseDir).getCanonicalFile();
			logger.info("oddjob.src=" + oddjobSrc.toString());
		}
		else {
			File pwd = new File(".").getCanonicalFile();
			if ("oddjob".equals(pwd.getName())) {
				logger.info("The appears to be the oddjob project.");
				oddjobSrc = new File(".");
			}
			else {
				logger.info("Guess oddjob.src to be a parrallel directory.");
				oddjobSrc = new OurDirs().relative("../oddjob").getCanonicalFile();
			}
		}
		
		if (!oddjobSrc.exists()) {
			throw new FileNotFoundException(oddjobSrc + " does not exist.");
		}
	}
	
	public File oddjobSrcBase() {
		return oddjobSrc;
	}
	
}
