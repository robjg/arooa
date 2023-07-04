package org.oddjob.arooa.beandocs;

/**
 * Documentation for a bean.
 */
public interface BeanDoc {

	String getName();
	
	String getPrefix();
	
	String getTag();
	
	String getClassName();
	
	String getFirstSentence();
	
	String getAllText();
		
	PropertyDoc[] getPropertyDocs();
	
	PropertyDoc propertyDocFor(String property);
	
	ExampleDoc[] getExampleDocs();
	
}
