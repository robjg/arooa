package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

/**
 * Documentation for a bean.
 */
public interface BeanDoc {

	String getName();
	
	String getPrefix();
	
	String getTag();
	
	String getClassName();
	
	List<BeanDocElement> getFirstSentence();

	List<BeanDocElement> getAllText();
		
	PropertyDoc[] getPropertyDocs();
	
	PropertyDoc propertyDocFor(String property);
	
	ExampleDoc[] getExampleDocs();
	
}
