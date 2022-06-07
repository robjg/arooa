package org.oddjob.arooa.deploy;

/**
 * The bean for the &lt;arooa:annotation&gt; definition in a descriptor.
 * 
 * @oddjob.description Create a synthetic annotation for a method.
 *   
 * @author rob
 *
 */
public class AnnotationDefinitionBean {

	/** 
     * @oddjob.property
     * @oddjob.description The name of the annotation.
     * @oddjob.required Yes.
	 */
	private String name;
	
	/** 
     * @oddjob.property
     * @oddjob.description The name of the method.
     * @oddjob.required Yes.
	 */
	private String method;
	
	/** 
     * @oddjob.property
     * @oddjob.description The name of the parameter type classes.
     * This is a comma separated list of class names. 
     * 
     * @oddjob.required No.
	 */
	private String parameterTypes;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setParameterTypes(String parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	
	public String getParameterTypes() {
		return parameterTypes;
	}
}
