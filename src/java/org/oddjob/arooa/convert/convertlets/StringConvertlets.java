package org.oddjob.arooa.convert.convertlets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.FinalConvertlet;
import org.oddjob.arooa.utils.ArooaTokenizer;
import org.oddjob.arooa.utils.QuoteTokenizerFactory;

public class StringConvertlets implements ConversionProvider {

	private final ArooaTokenizer tokenizer =  new QuoteTokenizerFactory(
			"\\s*,\\s*", '\"', '\\').newTokenizer();
	
	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Object.class, String.class, 
				new FinalConvertlet<Object, String>() {
			public String convert(Object from) throws ConvertletException {
				return from.toString();
			};
		});
		
		registry.register(String.class, InputStream.class, 
				new Convertlet<String, InputStream>() {
			public InputStream convert(String from) {
				return new ByteArrayInputStream(((String) from).getBytes());
			};
		});
		
		registry.register(String.class, String[].class, 
				new FinalConvertlet<String, String[]>() {
			public String[] convert(String from) throws ConvertletException {
				try {
					return tokenizer.parse(from);
				} catch (ParseException e) {
					throw new ConvertletException(e);
				}
			};
		});
	}
}
