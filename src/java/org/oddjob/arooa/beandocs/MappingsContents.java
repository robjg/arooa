package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;


/**
 * This is essentially a place holder for things
 * to come. At the moment documents are built in a javadoc style
 * but that doesn't really cope with Oddballs. This will also
 * provide a way of loading docs at runtime to provide help
 * in Designer.
 * <p>
 * 
 * @author rob
 *
 */
public interface MappingsContents {


	public ArooaElement[] allElements();
	
	public ArooaClass documentClass(ArooaElement element);
	
	
}
