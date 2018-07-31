package org.oddjob.arooa.deploy;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.oddjob.arooa.ArooaDescriptor;

/**
 * Create an {@link ArooaDescriptor} by scanning for arooa.xml
 * descriptor files.
 * <p>
 * This ArooaDescriptorFactory Returns null if there are no
 * descriptors on the class path.
 * 
 * @author rob
 *
 */
public class ClassPathDescriptorFactory 
implements ArooaDescriptorFactory {

	public static final String AROOA_FILE = "META-INF/arooa.xml";
	
	private String resource = AROOA_FILE;

	/** Exclude arooa.xml files in the parent class loader. */
	private boolean exludeParent;
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.deploy.ArooaDescriptorFactory#createDescriptor(java.lang.ClassLoader)
	 */
	public ArooaDescriptor createDescriptor(ClassLoader loader) {
		
		Enumeration<URL> allResources = null;
		Enumeration<URL> parentResources = null;
		
		try {
			allResources = loader.getResources(resource);
			if (exludeParent) {
				ClassLoader parent = loader.getParent();
				if (parent != null) {
					parentResources = parent.getResources(resource);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			
		Set<URL> urls = toSet(allResources);
		if (parentResources != null) {
			urls.removeAll(toSet(parentResources));
		
		}
		
		if (urls.size() == 0) {
			return null;
		}
		
		URLDescriptorFactory urlDescriptorFactory =
			new URLDescriptorFactory(urls);
		return urlDescriptorFactory.createDescriptor(loader);
	}
	
	private <T> Set<T> toSet(Enumeration<T> enumeration) {
		Set<T> set = new HashSet<T>();
		while (enumeration.hasMoreElements()) {
			T next = enumeration.nextElement();
			set.add(next);
		}
		
		return set;
	}
	
	public String getResource() {
		return resource;
	}

	/**
	 * Set the descriptor file to scan for. Defaults to 
	 * META-INF/arooa.xml
	 * 
	 * @param resource
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	public boolean isExludeParent() {
		return exludeParent;
	}

	/**
	 * Set to true to exclude arooa.xml files in the 
	 * parent class loader.
	 *  
	 * @param exludeParent
	 */
	public void setExcludeParent(boolean exludeParent) {
		this.exludeParent = exludeParent;
	}

}
