package org.oddjob.arooa.deploy;

import java.net.URL;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * Create an {link ArooaDescriptor} from a collection of URLs.
 * 
 * @author rob
 *
 */
public class URLDescriptorFactory 
implements ArooaDescriptorFactory {
	private static final Logger logger = 
		Logger.getLogger(URLDescriptorFactory.class);

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
	
	
	public ArooaDescriptor createDescriptor(ClassLoader classLoader) {

		if (urls.size() == 0) {
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
			catch (Exception e) {
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
