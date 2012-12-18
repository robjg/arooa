package org.oddjob.arooa.convert.jokers;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.NoConversionAvailableException;

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
	
	/**
	 * What's a Shadow Joker? I think this means something the masks the
	 * default conversion.
	 * 
	 * @throws NoConversionAvailableException
	 * @throws ConversionFailedException
	 */
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
	
	interface Colour {
	}

	enum Colours implements Colour {
		
		RED,
		BLUE,
		GREEN,
	}
	
	class ColourConversions implements ConversionProvider {
		
		@Override
		public void registerWith(ConversionRegistry registry) {
			registry.register(String.class, Colours.class, new Convertlet<String, Colours>() {
				@Override
				public Colours convert(String from) throws ConvertletException {
					return Colours.valueOf(from);
				}
			});
		}
	}
	
	/**
	 * Showing special conversion is necessary otherwise the default converter
	 * has no idea which enum to convert to, to match the interface.
	 * 
	 * @throws NoConversionAvailableException
	 * @throws ConversionFailedException
	 */
	public void testAnEnumThatImplementsAnInterface() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();

		new ColourConversions().registerWith(registry);
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		ConversionPath<String, Colour> path = converter.findConversion(
				String.class, Colour.class);
		
		assertEquals("String-Colours", path.toString());
		
		assertEquals(Colours.RED, converter.convert("RED", Colour.class));
	}
}
