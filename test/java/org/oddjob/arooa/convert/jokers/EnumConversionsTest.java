package org.oddjob.arooa.convert.jokers;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.convert.jokers.EnumConversions;

public class EnumConversionsTest extends TestCase {

	enum Deed {
		GOOD,
		BAD
	}
	
	public void testStringConversions() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();

		new EnumConversions().registerWith(registry);
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		assertEquals(Deed.GOOD, converter.convert("GOOD", Deed.class));
		
		assertEquals("BAD", converter.convert(Deed.BAD, String.class));
	}
	
	public void testShadowJoker() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();

		new EnumConversions().registerWith(registry);
		registry.registerJoker(Deed.class, new Joker<Deed>() {
			public <T> ConversionStep<Deed, T> lastStep(
					final Class<? extends Deed> from,
					final Class<T> to, 
					ConversionLookup conversions) {
				
				if (String.class.isAssignableFrom(to)) {
					return new ConversionStep<Deed, T>() {
						public Class<Deed> getFromClass() {
							return Deed.class;
						}
						public Class<T> getToClass() {
							return to;
						}
						@SuppressWarnings("unchecked")
						public T convert(Deed from, ArooaConverter converter)
								throws ArooaConversionException {
							switch (from) {
							case GOOD:
								return (T) "Have A Medel";
							case BAD:
								return (T) "Go To Jail";
							}
							return null;
						}
					};
				}
				return null;
			}
		});
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		assertEquals("Have A Medel", converter.convert(Deed.GOOD, String.class));
		assertEquals("Go To Jail", converter.convert(Deed.BAD, String.class));
		
	}

	public void testShadowConversion() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();

		new EnumConversions().registerWith(registry);
		registry.register(Deed.class, String.class,
				new Convertlet<Deed, String>() {
					public String convert(Deed from) throws ConvertletException {
					switch (from) {
					case GOOD:
						return "Have A Medel";
					case BAD:
						return "Go To Jail";
					}
					return null;
				};
			});
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		assertEquals("Have A Medel", converter.convert(Deed.GOOD, String.class));
		assertEquals("Go To Jail", converter.convert(Deed.BAD, String.class));
		
	}

	enum GradedDeed {
		ANGELIC(10),
		GOOD(5),
		BAD(-5),
		EVIL(-10)
		;
		
		final int score;
		
		GradedDeed(int score) {
			this.score = score;
		}
		
		@Override
		public String toString() {
			return super.toString() + ": " + score;
		}
	}

	public void testComplicatedDeed() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();

		new EnumConversions().registerWith(registry);
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		assertEquals(GradedDeed.GOOD, converter.convert("GOOD", GradedDeed.class));
		
		assertEquals("BAD: -5", converter.convert(GradedDeed.BAD, String.class));
	}

}
