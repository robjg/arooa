package org.oddjob.arooa.convert;

import org.oddjob.arooa.convert.convertlets.*;
import org.oddjob.arooa.convert.jokers.ArrayConversions;
import org.oddjob.arooa.convert.jokers.EnumConversions;
import org.oddjob.arooa.types.*;

/**
 * A {@link ConversionProvider} for the default conversions.
 * The default conversions are:
 * <ul>
 *  <li>{@link BooleanConvertlets}</li>
 *  <li>{@link ByteConvertlets}</li>
 *  <li>{@link CharacterConvertlets}</li>
 *  <li>{@link CollectionConvertlets}</li>
 *  <li>{@link DateConvertlets}</li>
 *  <li>{@link DoubleConvertlets}</li>
 *  <li>{@link FileConvertlets}</li>
 *  <li>{@link FloatConvertlets}</li>
 *  <li>{@link IntegerConvertlets}</li>
 *  <li>{@link LongConvertlets}</li>
 *  <li>{@link ShortConvertlets}</li>
 *  <li>{@link BigDecimalConvertlets}</li>
 *  <li>{@link StringConvertlets}</li>
 *  <li>{@link SqlDateConvertlets}</li>
 *  <li>{@link URIConvertlets}</li>
 *  <li>{@link URLConvertlets}</li>
 * </ul>
 * The conversions provided by the types:
 * <ul>
 *  <li>{@link ValueType}</li>
 *  <li>{@link XMLType}</li>
 *  <li>{@link ImportType}</li>
 *  <li>{@link ListType}</li>
 *  <li>{@link MapType}</li>

 * </ul>
 * Some general conversions:
 * <ul>
 *  <li>{@link EnumConversions}</li>
 *  <li>{@link ArrayConversions}</li>
 * <ul>
 * And some special conversions provided by:
 * <ul>
 *  <li>{@link ArooaValueConvertlets()}</li>
 *  <li>{@link ArooaObject.Conversions}</li>
 *  <li>{@link ValueFactory}</li>
 *  <li>{@link IdentifiableValueType}</li>
 * <ul>
 * 
 * @author rob
 *
 */
public class DefaultConversionProvider implements ConversionProvider {

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.convert.ConvertletProvider#registerWith(org.oddjob.arooa.convert.ConvertletRegistry, org.oddjob.arooa.ArooaSession)
	 */
	public void registerWith(ConversionRegistry registry) {
		new BooleanConvertlets().registerWith(registry);
		new ByteConvertlets().registerWith(registry);
		new CharacterConvertlets().registerWith(registry);
		new DoubleConvertlets().registerWith(registry);
		new FloatConvertlets().registerWith(registry);
		new IntegerConvertlets().registerWith(registry);
		new LongConvertlets().registerWith(registry);
		new ShortConvertlets().registerWith(registry);
		new DateConvertlets().registerWith(registry);
		new BigDecimalConvertlets().registerWith(registry);
		new StringConvertlets().registerWith(registry);
		new FileConvertlets().registerWith(registry);
		new PathConvertlets().registerWith(registry);
		new URIConvertlets().registerWith(registry);
		new URLConvertlets().registerWith(registry);
		new SqlDateConvertlets().registerWith(registry);
		new CollectionConvertlets().registerWith(registry);
		// The Default Types
		new ValueType.Conversions().registerWith(registry);
		new XMLType.Conversions().registerWith(registry);
		new ImportType.Conversions().registerWith(registry);
		new ListType.Conversions().registerWith(registry);
		new MapType.Conversions().registerWith(registry);
		new ClassType.Conversions().registerWith(registry);
		new ConvertType.Conversions().registerWith(registry);
		// Some general conversions.
		new EnumConversions().registerWith(registry);
		new ArrayConversions().registerWith(registry);
		// Some special conversions.
		new ArooaValueConvertlets().registerWith(registry);
		new ArooaObject.Conversions().registerWith(registry);
		new ValueFactory.Conversions().registerWith(registry);
		new IdentifiableValueType.Conversions().registerWith(registry);
		new ConversionConvertlets().registerWith(registry);
		// Would be nice to do this another way
		new NashornConvertlets().registerWith(registry);
	}
}
