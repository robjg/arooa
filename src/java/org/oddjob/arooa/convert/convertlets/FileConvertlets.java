/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.FinalConvertlet;

public class FileConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(String.class, File.class, 
				new Convertlet<String, File>() {
			public File convert(String from) {
				return new File(from);
			};
		});
		
		registry.register(File.class, InputStream.class, 
				new Convertlet<File, InputStream>() {
			public InputStream convert(final File from) throws ConvertletException {
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
			};
		});
		
		registry.register(File.class, OutputStream.class, 
				new Convertlet<File, OutputStream>() {
			public OutputStream convert(final File from) throws ConvertletException {
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
			}
		});
		
		registry.register(File.class, URL.class, 
				new Convertlet<File, URL>() {
			public URL convert(File from) throws ConvertletException {
				try {
					return from.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new ConvertletException(e);
				}
			}
		});
		
		registry.register(File.class, File[].class, 
				new FinalConvertlet<File, File[]>() {
			public File[] convert(File from) {
				return new File[] { from };
			};
		});
		
		registry.register(String.class, File[].class, 
				new FinalConvertlet<String, File[]>() {
			public File[] convert(String from) {
				return pathToFiles(from);
			};
		});
		
		registry.register(File[].class, String.class, 
				new FinalConvertlet<File[], String>() {
			public String convert(File[] from) {
				StringBuilder path = new StringBuilder();
				for (File file : from) {
					if (path.length() > 0) {
						path.append(File.pathSeparator);
					}
					path.append(file.toString());
				}
				return path.toString();
			};
		});
	}

	public File[] pathToFiles(String from) {
		String[] strings = from.split(File.pathSeparator);
		File[] files = new File[strings.length];
		for (int i = 0; i < strings.length; ++i) {
			files[i] = new File(strings[i]);
		}
		return files;
	}
}
