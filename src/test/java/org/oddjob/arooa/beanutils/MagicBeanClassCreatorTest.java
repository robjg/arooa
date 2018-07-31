package org.oddjob.arooa.beanutils;

import org.junit.Test;

import org.junit.Assert;

import org.apache.commons.beanutils.DynaBean;
import org.oddjob.arooa.reflect.ArooaClass;

public class MagicBeanClassCreatorTest extends Assert {

   @Test
	public void testAllPrimatives() {
		
		MagicBeanClassCreator creator = new MagicBeanClassCreator("Test");
		
		creator.addProperty("boolean", boolean.class);
		creator.addProperty("byte", byte.class);
		creator.addProperty("char", char.class);
		creator.addProperty("short", short.class);
		creator.addProperty("int", int.class);
		creator.addProperty("long", long.class);
		creator.addProperty("float", float.class);
		creator.addProperty("double", double.class);

		ArooaClass arooaClass = creator.create();
		
		DynaBean bean = (DynaBean) arooaClass.newInstance();
		
		bean.set("boolean", true);
		bean.set("byte", (byte) 1);
		bean.set("char", 'A');
		bean.set("short", (short) 2);
		bean.set("int", 3 );
		bean.set("long", 4L);
		bean.set("float", 1.1F);
		bean.set("double", 2.2);
		
		assertEquals(new Boolean(true), bean.get("boolean"));
		assertEquals(new Byte((byte) 1), bean.get("byte"));
		assertEquals(new Character('A'), bean.get("char"));
		assertEquals(new Short((short) 2), bean.get("short"));
		assertEquals(new Integer(3), bean.get("int"));
		assertEquals(new Long(4L), bean.get("long"));
		assertEquals(new Float(1.1F), bean.get("float"));
		assertEquals(new Double(2.2), bean.get("double"));
	}
}
