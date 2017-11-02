package org.oddjob.arooa.design;

import org.junit.Test;

import java.io.IOException;

import org.junit.Assert;

import org.oddjob.ArooaTestHelper;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;

public class SerializableGenericDesignFactoryTest extends Assert {

	public static class Fruit {
		
		public void setColour(String colour) {};
	}
	
   @Test
	public void testSerialize() throws IOException, ClassNotFoundException {
		
		SerializableGenericDesignFactory test = 
				new SerializableGenericDesignFactory(Fruit.class);
		
		
		
		SerializableGenericDesignFactory copy = 
				ArooaTestHelper.copy(test);
		
		assertNotNull(copy);

		DesignInstance design = copy.createDesign(new ArooaElement("fruit"), 
				new DesignSeedContext(ArooaType.COMPONENT, 
						new StandardArooaSession()));
		
		Form form = design.detail();
		
		assertNotNull(form);
	}
}
