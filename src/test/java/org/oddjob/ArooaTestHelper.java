package org.oddjob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.standard.StandardFragmentParser;

/**
 * Useful utility methods and constants for tests.
 * 
 * @author Rob Gordon.
 */
public class ArooaTestHelper {

	/**
	 * Copy an object using serialization.
	 * 
	 * @param object The object.
	 * 
	 * @return The copy.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
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
	
	/**
	 * Create a value.
	 * 
	 * @param config
	 * @return
	 * @throws ArooaParseException
	 */
    public static Object createValueFromConfiguration(ArooaConfiguration config) 
    throws ArooaParseException {
    	return createValueFromConfiguration(config, null);
    }
    
    public static Object createValueFromConfiguration(ArooaConfiguration config,
    		ArooaDescriptor descriptor) 
    throws ArooaParseException {
	    
    	StandardFragmentParser parser = new StandardFragmentParser(descriptor);
    	
    	parser.setArooaType(ArooaType.VALUE);
    	
    	parser.parse(config);

    	return parser.getRoot();        
    }
}
