package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.FinalConvertlet;
import org.oddjob.arooa.utils.ArooaTokenizer;
import org.oddjob.arooa.utils.QuoteTokenizerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;

public class StringConvertlets implements ConversionProvider {

	private final ArooaTokenizer tokenizer =  new QuoteTokenizerFactory(
			"\\s*,\\s*", '\"', '\\').newTokenizer();
	
	public void registerWith(ConversionRegistry registry) {

		// TODO: We must get rid of this!
		registry.register(Object.class, String.class,
				(FinalConvertlet<Object, String>) Object::toString);
		
		registry.register(String.class, InputStream.class,
				from -> new ByteArrayInputStream(from.getBytes()));
		
		registry.register(String.class, String[].class,
				(FinalConvertlet<String, String[]>) from -> {
					try {
						return tokenizer.parse(from);
					} catch (ParseException e) {
						throw new ConvertletException(e);
					}
				});
	}
}
