/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.FinalConvertlet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class FileConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(String.class, File.class,
				File::new);

		registry.register(File.class, String.class,
				(FinalConvertlet<File, String>) File::toString);

		registry.register(File.class, InputStream.class,
				from -> {
					try {
						return new BufferedInputStream(
								new FileInputStream(from)) {
							@Override
							public String toString() {
								return "BufferedFileInput from " + from.getAbsolutePath();
							}
						};
					} catch (FileNotFoundException e) {
						throw new ConvertletException(e);
					}
				});
		
		registry.register(File.class, OutputStream.class,
				from -> {
					try {
						return new BufferedOutputStream(
								new FileOutputStream(from)) {
							@Override
							public String toString() {
								return "BufferedFileOutput to " + from.getAbsolutePath();
							}
						};
					} catch (FileNotFoundException e) {
						throw new ConvertletException(e);
					}
				});
		
		registry.register(File.class, URL.class,
				from -> {
					try {
						return from.toURI().toURL();
					} catch (MalformedURLException e) {
						throw new ConvertletException(e);
					}
				});
		
		registry.register(File.class, File[].class,
				(FinalConvertlet<File, File[]>) from -> new File[] { from });
		
		registry.register(String.class, File[].class,
				(FinalConvertlet<String, File[]>) FileConvertlets::pathToFiles);
		
		registry.register(File[].class, String.class,
				(FinalConvertlet<File[], String>) FileConvertlets::filesToPath);
	}

	public static String filesToPath(File... from) {
		StringBuilder path = new StringBuilder();
		for (File file : from) {
			if (path.length() > 0) {
				path.append(File.pathSeparator);
			}
			path.append(file.toString());
		}
		return path.toString();		
	}
	
	public static File[] pathToFiles(String from) {
		String[] strings = from.split(File.pathSeparator);
		File[] files = new File[strings.length];
		for (int i = 0; i < strings.length; ++i) {
			files[i] = new File(strings[i]);
		}
		return files;
	}
}
