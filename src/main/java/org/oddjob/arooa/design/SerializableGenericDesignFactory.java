package org.oddjob.arooa.design;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ConfigurationOwner;
import org.oddjob.arooa.parsing.SerializableDesignFactory;

/**
 * A {@link GenericDesignFactory} that is an {@link SerializableGenericDesignFactory}.
 * 
 * @see ConfigurationOwner
 * 
 * @author rob
 *
 */
public class SerializableGenericDesignFactory 
extends GenericDesignFactory
implements SerializableDesignFactory {

	private Class<?> theClass;
	
	public SerializableGenericDesignFactory(Class<?> theClass) {
		super(new SimpleArooaClass(theClass));
		this.theClass = theClass;
	}
	
	private Object writeReplace()
	throws ObjectStreamException {
		return new WriteReplacement(theClass);
	}
	
    private static final class WriteReplacement implements Serializable {
    	private static final long serialVersionUID = 2013120800;
    	
    	private Class<?> theClass;
    	
    	private WriteReplacement(Class<?> theClass) {
    		this.theClass = theClass;
		}
    	
    	private WriteReplacement() {
		}
    	
    	private Object readResolve()
        		throws ObjectStreamException {
    		
    		return new SerializableGenericDesignFactory(theClass);
    	}
    }
  		 
}
