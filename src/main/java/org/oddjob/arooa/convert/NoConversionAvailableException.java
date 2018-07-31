package org.oddjob.arooa.convert;

public class NoConversionAvailableException extends ArooaConversionException {
	private static final long serialVersionUID = 20070219;
	
	private final Class<?> fromClass;
	private final Class<?> toClass;
	
	public NoConversionAvailableException(Class<?> fromClass, Class<?> toClass) {
		this.fromClass = fromClass;
		this.toClass = toClass;
	}
	
	public Class<?> getFromClass() {
		return fromClass;
	}
	
	public Class<?> getToClass() {
		return toClass;
	}
	
	public String getMessage() {
		return "There is no conversion available between [" +
				fromClass.getName() + "] and [" + toClass.getName() + "]";
	}
}
