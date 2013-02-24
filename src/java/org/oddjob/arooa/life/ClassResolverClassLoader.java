package org.oddjob.arooa.life;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import org.oddjob.arooa.ClassResolver;

public class ClassResolverClassLoader extends ClassLoader {

	private final ClassResolver classResolver;
	
	public ClassResolverClassLoader(ClassResolver classResolver) {
		this.classResolver = classResolver;
	}
	
	@Override
	protected synchronized Class<?> findClass(String name)
			throws ClassNotFoundException {
		
		Class<?> theClass = classResolver.findClass(name);
		
		if (theClass == null) {
			throw new ClassNotFoundException(name);
		}
		
		return theClass;
	}
	
	@Override
	protected URL findResource(String name) {
		
		return classResolver.getResource(name);
	}
	
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		
		URL[] urls = classResolver.getResources(name);
		return new Vector<URL>(Arrays.asList(urls)).elements();
		
	}
}
