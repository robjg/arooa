package org.oddjob.arooa.beandocs;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.*;

import java.util.HashMap;
import java.util.Map;

public class SessionArooaDocFactoryTest extends Assert {

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
		MatcherAssert.assertThat(beanProperties.get(BeanType.ATTRIBUTE),
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

}
