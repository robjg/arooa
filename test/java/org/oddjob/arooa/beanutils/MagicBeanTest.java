package org.oddjob.arooa.beanutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.apache.commons.beanutils.DynaProperty;

public class MagicBeanTest extends TestCase {

	public void testSerialisation() throws IOException, ClassNotFoundException {
		
		MagicBeanClass beanClass = new MagicBeanClass(
				new DynaProperty[] { new DynaProperty("fruit", String.class), 
				}, "snack");
		
		MagicBean test = new MagicBean(beanClass);

		BeanUtilsPropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		accessor.setProperty(test, "fruit", "apple");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ObjectOutputStream oo = new ObjectOutputStream(baos);
		
		oo.writeObject(test);
	
		ObjectInputStream oi = new ObjectInputStream(
				new ByteArrayInputStream(baos.toByteArray()));
				
		Object copy = oi.readObject();
		
		Object fruit = accessor.getProperty(copy, "fruit");
		
		assertEquals("apple", fruit);
	}
	
	public void testToString() {
		
		MagicBeanClass beanClass = new MagicBeanClass(
				new DynaProperty[] { }, "snack");
		
		MagicBean test = new MagicBean(beanClass);
		
		assertEquals("MagicBean:snack@" + Integer.toHexString(test.hashCode()), 
				test.toString());
	}
	
}
