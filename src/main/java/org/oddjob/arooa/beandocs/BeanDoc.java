package org.oddjob.arooa.beandocs;

public interface BeanDoc {

	public String getName();
	
	public String getPrefix();
	
	public String getTag();
	
	public String getClassName();
	
	public String getFirstSentence();
	
	public String getAllText();
		
	public PropertyDoc[] getPropertyDocs();
	
	public PropertyDoc propertyDocFor(String property);
	
	public ExampleDoc[] getExampleDocs();
	
}
