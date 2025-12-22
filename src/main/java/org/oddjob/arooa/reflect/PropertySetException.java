/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.reflect;


import java.io.Serial;
import java.lang.reflect.Type;

/**
 *
 */
public class PropertySetException extends ArooaPropertyException {
    @Serial
    private static final long serialVersionUID = 20070205;

    private final Type propertyType;

    private final Object value;

    public PropertySetException(Object bean, String property,
                                Type propertyType, Object value, Throwable cause) {
        super(property,
                "Failed setting property [" + property + "] of type (" +
                        propertyType.getTypeName() + ") in class (" +
                        bean.getClass().getName() + ") with value [" + value + "]" +
                        (value == null ? "" : " of type (" +
                                value.getClass().getName() + ")"),
                cause);
        this.propertyType = propertyType;
        this.value = value;
    }

    public Type getPropertyType() {
        return propertyType;
    }

    public Object getValue() {
        return value;
    }
}
