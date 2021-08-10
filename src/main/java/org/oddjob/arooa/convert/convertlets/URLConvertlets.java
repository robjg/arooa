/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.FinalConvertlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URLConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(String.class, URL.class,
				from -> {
					try {
						return new URL(from);
					} catch (MalformedURLException e) {
						throw new ConvertletException(e);
					}
				});

		registry.register(URL.class, String.class,
				(FinalConvertlet<URL, String>) URL::toString);

		registry.register(URL.class, InputStream.class,
				from -> {
					try {
						return from.openStream();
					} catch (IOException e) {
						throw new ConvertletException(e);
					}
				});
		
		registry.register(URL.class, URI.class,
				from -> {
					try {
						return from.toURI();
					} catch (URISyntaxException e) {
						throw new ConvertletException(e);
					}
				});
	}
	
}
