package org.oddjob.arooa.beandocs;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.convert.convertlets.BooleanConvertlets;
import org.oddjob.arooa.convert.doc.TypeIdentifier;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SessionArooaDocFactoryTest {

    @Test
	public void testStandardSession() {
		
		ArooaSession session = new StandardArooaSession();
		
		SessionArooaDocFactory test = new SessionArooaDocFactory(session);
		
		WriteableArooaDoc arooaDoc = test.createBeanDocs(ArooaType.VALUE);
		
		Map<String, WriteableBeanDoc> results =
				new HashMap<>();
		
		for (WriteableBeanDoc beanDoc: arooaDoc.getBeanDocs()) {
			results.put(beanDoc.getClassName(), beanDoc);
		}
		
		assertEquals(11, results.size());
		
		// Is
		
		BeanDoc isDoc = results.get(IsType.class.getName());
		
		Map<String, PropertyDoc> isProperties = new HashMap<>();
		
		for (PropertyDoc propertyDoc: isDoc.getPropertyDocs()) {
			isProperties.put(propertyDoc.getPropertyName(), propertyDoc);
		}
		
		assertEquals(0, isProperties.size());
		
		// Bean
		
		BeanDoc beanDoc = results.get(BeanType.class.getName());
		
		Map<String, PropertyDoc> beanProperties = new HashMap<>();
		
		for (PropertyDoc propertyDoc: beanDoc.getPropertyDocs()) {
			beanProperties.put(propertyDoc.getPropertyName(), propertyDoc);
		}
		
		assertEquals(1, beanProperties.size());
		assertThat(beanProperties.get(BeanType.ATTRIBUTE),
				Matchers.notNullValue());

		// Class
		
		WriteableBeanDoc classDoc = results.get(ClassType.class.getName());
		
		Map<String, WriteablePropertyDoc> classProperties =
				new HashMap<>();
		
		for (WriteablePropertyDoc propertyDoc: classDoc.getPropertyDocs()) {
			classProperties.put(propertyDoc.getPropertyName(), propertyDoc);
		}
		
		assertEquals(3, classProperties.size());
		
		WriteablePropertyDoc classNamePropertyDoc = 
			classProperties.get("name");
		assertEquals(PropertyDoc.Multiplicity.SIMPLE,
				classNamePropertyDoc.getMultiplicity());
		assertEquals(PropertyDoc.Access.READ_WRITE,
				classNamePropertyDoc.getAccess());
		assertEquals(ConfiguredHow.ATTRIBUTE,
				classNamePropertyDoc.getConfiguredHow());		
		
		WriteablePropertyDoc classClassLoaderPropertyDoc = 
			classProperties.get("classLoader");
		assertEquals(PropertyDoc.Multiplicity.SIMPLE,
				classClassLoaderPropertyDoc.getMultiplicity());
		assertEquals(PropertyDoc.Access.READ_WRITE,
				classClassLoaderPropertyDoc.getAccess());
		assertEquals(ConfiguredHow.ELEMENT,
				classClassLoaderPropertyDoc.getConfiguredHow());		
		
		WriteablePropertyDoc classSessionPropertyDoc = 
			classProperties.get("arooaSession");
		assertEquals(PropertyDoc.Multiplicity.SIMPLE,
				classSessionPropertyDoc.getMultiplicity());
		assertEquals(PropertyDoc.Access.WRITE_ONLY,
				classSessionPropertyDoc.getAccess());
		assertEquals(ConfiguredHow.HIDDEN,
				classSessionPropertyDoc.getConfiguredHow());		
		
		// List
		
		WriteableBeanDoc listDoc = results.get(ListType.class.getName());
		
		Map<String, WriteablePropertyDoc> listProperties =
				new HashMap<>();
		
		for (WriteablePropertyDoc propertyDoc: listDoc.getPropertyDocs()) {
			listProperties.put(propertyDoc.getPropertyName(), propertyDoc);
		}
		
		
		WriteablePropertyDoc listValuesPropertyDoc = 
			listProperties.get("values");
		assertEquals(PropertyDoc.Multiplicity.INDEXED,
				listValuesPropertyDoc.getMultiplicity());
		assertEquals(PropertyDoc.Access.READ_WRITE,
				listValuesPropertyDoc.getAccess());
		assertEquals(ConfiguredHow.ELEMENT,
				listValuesPropertyDoc.getConfiguredHow());
		
		WriteablePropertyDoc listUniquePropertyDoc = 
			listProperties.get("unique");
		assertEquals(PropertyDoc.Multiplicity.SIMPLE,
				listUniquePropertyDoc.getMultiplicity());
		assertEquals(PropertyDoc.Access.READ_WRITE,
				listUniquePropertyDoc.getAccess());
		assertEquals(ConfiguredHow.ATTRIBUTE,
				listUniquePropertyDoc.getConfiguredHow());
		
		// Map

		WriteableBeanDoc mapDoc = results.get(MapType.class.getName());

		Map<String, WriteablePropertyDoc> mapProperties =
				new HashMap<>();

		for (WriteablePropertyDoc propertyDoc: mapDoc.getPropertyDocs()) {
			mapProperties.put(propertyDoc.getPropertyName(), propertyDoc);
		}


		WriteablePropertyDoc mapValuesPropertyDoc = 
				mapProperties.get("values");
		assertEquals(PropertyDoc.Multiplicity.MAPPED,
				mapValuesPropertyDoc.getMultiplicity());
		assertEquals(PropertyDoc.Access.READ_WRITE,
				mapValuesPropertyDoc.getAccess());
		assertEquals(ConfiguredHow.ELEMENT,
				mapValuesPropertyDoc.getConfiguredHow());

		WriteablePropertyDoc mapElementTypePropertyDoc = 
				mapProperties.get("elementType");
		assertEquals(PropertyDoc.Multiplicity.SIMPLE,
				mapElementTypePropertyDoc.getMultiplicity());
		assertEquals(PropertyDoc.Access.READ_WRITE,
				mapElementTypePropertyDoc.getAccess());
		assertEquals(ConfiguredHow.ELEMENT,
				mapElementTypePropertyDoc.getConfiguredHow());
	}

    @Test
    void conversions() throws ClassNotFoundException {

        StandardArooaSession session = new StandardArooaSession();

        SessionArooaDocFactory test = new SessionArooaDocFactory(session);

        WriteableConversionDocs conversionsByType = test.createConversionDocs();

        assertThat(conversionsByType.containsDocumentedByType(TypeIdentifier.ofClass(InlineType.class)),
                is(true));

        ConversionDoc[] docs = conversionsByType.getConversionDocs();

        Map<String, List<ConversionDoc>> conversions = Arrays.stream(docs)
                .collect(Collectors.groupingBy(ConversionDoc::getFromType));

        List<ConversionDoc> numberToBooleans = conversions.get(Number.class.getTypeName())
                .stream()
                .filter(conversionDoc -> Boolean.class.getTypeName().equals(conversionDoc.getToType()))
                .toList();

        assertThat(numberToBooleans.size(), is(1));

        ConversionDoc numberToBoolean = numberToBooleans.getFirst();

        Class<?> cl = Class.forName(BooleanConvertlets.class.getCanonicalName() + "$NumberToBoolean");
        TypeIdentifier typeIdentifier = TypeIdentifier.ofClass(cl);

        WriteableConversionDoc docByType = conversionsByType
                .conversionDocumentedByType(typeIdentifier);

        assertThat(docByType, sameInstance(numberToBoolean));

        ConversionDoc inlineConversion = conversions.get(InlineType.class.getTypeName())
                .getFirst();

        assertThat(inlineConversion.getToType(), is(ArooaConfiguration.class.getTypeName()));
    }

}
