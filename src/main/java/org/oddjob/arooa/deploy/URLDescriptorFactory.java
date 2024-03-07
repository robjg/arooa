package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

/**
 * Create an {link ArooaDescriptor} from a collection of URLs.
 * 
 * @author rob
 *
 */
public class URLDescriptorFactory 
implements ArooaDescriptorFactory {
	private static final Logger logger = 
		LoggerFactory.getLogger(URLDescriptorFactory.class);

	private final Collection<URL> urls;

	/**
	 * @param urls The collection of URLs. Must not be null.
	 */
	public URLDescriptorFactory(Collection<URL> urls) {
		if (urls == null) {
			throw new NullPointerException("URLs must not be null.");
		}
		this.urls = urls;
	}
	
	/**
	 * Constructor for individual URLs.
	 * 
	 * @param urls The urls.
	 */
	public URLDescriptorFactory(URL... urls) {
		this(Arrays.asList(urls));
	}
	
	public ArooaDescriptor createDescriptor(ClassLoader classLoader) {

		if (urls.isEmpty()) {
			return null;
		}
		
		ListDescriptor listDescriptor = 
			new ListDescriptor();
						
		for (URL url: urls ) {
			try {

				logger.debug("Reading ArooaDescriptor [" + url + "].");

				ArooaDescriptor descriptor = 
						new ConfigurationDescriptorFactory(
								new XMLConfiguration(url)).createDescriptor(
										classLoader);

				if (urls.size() == 1) {
					return descriptor;
				}

				listDescriptor.addDescriptor(descriptor);
			}
			catch (Throwable e) {
				throw new RuntimeException(
						"Failed creating descriptor factory from url " + 
						url, e);
			}
		}

		return listDescriptor;
	}
	
	public Collection<URL> getUrls() {
		return urls;
	}

}
