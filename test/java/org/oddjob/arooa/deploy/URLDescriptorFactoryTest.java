package org.oddjob.arooa.deploy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.OurDirs;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class URLDescriptorFactoryTest extends TestCase {
	private static final Logger logger = Logger.getLogger(URLDescriptorFactoryTest.class);
	
	public void testClassLoader() throws IOException {

		OurDirs ourDirs = new OurDirs();
		
		File classes = new File(ourDirs.base(), "build/test");
		if (!classes.exists()) {
			classes = new File(ourDirs.base(), "classes");
		}
		if (!classes.exists()) {
			throw new IllegalStateException("No classes!");
		}
		
		URLClassLoader classLoader = new URLClassLoader(new URL[] {
				classes.toURI().toURL()
		}, null);
		
		String resource = "org/oddjob/arooa/deploy/URLDescriptorFactoryTest.xml";
		
		Enumeration<URL> eUrls = classLoader.getResources(resource);

		List<URL> urls = new ArrayList<URL>();
		
		while (eUrls.hasMoreElements()) {
			URL url = eUrls.nextElement();
			logger.info("Adding " + url);
			urls.add(url);
		}
		
		assertEquals(1, urls.size());

		URLDescriptorFactory test = new URLDescriptorFactory(urls);
		
		ArooaDescriptor descriptor = test.createDescriptor(classLoader);
				
		ArooaClass result = descriptor.getElementMappings(
				).mappingFor(new ArooaElement("ours"),
						new InstantiationContext(ArooaType.COMPONENT, null));
				
		assertEquals(classLoader, 
				((SimpleArooaClass) result).forClass().getClassLoader());
	}	
}
