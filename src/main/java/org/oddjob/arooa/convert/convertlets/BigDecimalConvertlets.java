package org.oddjob.arooa.convert.convertlets;

import java.math.BigDecimal;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.FinalConvertlet;

public class BigDecimalConvertlets implements ConversionProvider {

	@Override
	public void registerWith(ConversionRegistry registry) {
		
		registry.register(String.class, BigDecimal.class, 
				new Convertlet<String, BigDecimal>() {
			@Override
			public BigDecimal convert(String from)
			throws ConvertletException {
				return new BigDecimal(from);
			}	
		});
		
		registry.register(Long.class, BigDecimal.class, 
				new Convertlet<Long, BigDecimal>() {
			@Override
			public BigDecimal convert(Long from)
			throws ConvertletException {
				return new BigDecimal(from);
			}	
		});
		
		registry.register(Integer.class, BigDecimal.class, 
				new Convertlet<Integer, BigDecimal>() {
			@Override
			public BigDecimal convert(Integer from)
			throws ConvertletException {
				return new BigDecimal(from);
			}	
		});
		
		registry.register(Double.class, BigDecimal.class, 
				new Convertlet<Double, BigDecimal>() {
			@Override
			public BigDecimal convert(Double from)
			throws ConvertletException {
				return new BigDecimal(from);
			}	
		});
		
		registry.register(BigDecimal.class, String.class, 
				new FinalConvertlet<BigDecimal, String>() {
			@Override
			public String convert(BigDecimal from)
			throws ConvertletException {
				return from.toString();
			}	
		});	
	}
}
