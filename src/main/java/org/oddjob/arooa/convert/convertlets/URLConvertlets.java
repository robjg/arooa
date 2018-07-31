/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;

public class URLConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(String.class, URL.class, 
				new Convertlet<String, URL>() {
			public URL convert(String from) throws ConvertletException {
				try {
					return new URL(from);
				} catch (MalformedURLException e) {
					throw new ConvertletException(e);
				}
			};
		});
		
		registry.register(URL.class, InputStream.class, 
				new Convertlet<URL, InputStream>() {
			public InputStream convert(URL from) throws ConvertletException {
				try {
					return from.openStream();
				} catch (IOException e) {
					throw new ConvertletException(e);
				}
			};
		});
		
		registry.register(URL.class, URI.class, 
				new Convertlet<URL, URI>() {
			public URI convert(URL from) throws ConvertletException {
				try {
					return from.toURI();
				} catch (URISyntaxException e) {
					throw new ConvertletException(e);
				}
			};
		});
	}
	
}
