package org.oddjob.arooa.convert;

import java.io.Serial;
import java.lang.reflect.Type;

public class NoConversionAvailableException extends ArooaConversionException {
	@Serial
    private static final long serialVersionUID = 20070219;
	
	private final TypeArooa<?> fromType;
	private final Type toType;
	
	public NoConversionAvailableException(Class<?> fromClass, Type toType) {
        this(TypeArooa.of(fromClass), toType);
	}

    public NoConversionAvailableException(TypeArooa<?> fromType, Type toType) {
        this.fromType = fromType;
        this.toType = toType;
    }

	public TypeArooa<?> getFromType() {
		return fromType;
	}
	
	public Type getToType() {
		return toType;
	}

	public String getMessage() {
		return "There is no conversion available between [" +
				fromType.getType().getTypeName() + "] and [" + toType.getTypeName() + "]";
	}
}
