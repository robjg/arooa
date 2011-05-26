/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.net.URI;
import java.net.URISyntaxException;

import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;

public class URIConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(String.class, URI.class, 
				new Convertlet<String, URI>() {
			public URI convert(String from) throws ConvertletException {
				try {
					return new URI(from);
				} catch (URISyntaxException e) {
					throw new ConvertletException(e);
				}
			};
		});
		
	}
	
}
