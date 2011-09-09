package org.oddjob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class TestHelper {

	@SuppressWarnings("unchecked")
	public static <T> T copy(T object) throws IOException, ClassNotFoundException {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(out);
		oo.writeObject(object);
		oo.close();
			
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		ObjectInput oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return (T) o;
    }
	
}
