package org.oddjob.arooa;

import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ExpressionParser;

public class MockArooaTools implements ArooaTools {

	public ArooaConverter getArooaConverter() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public PropertyAccessor getPropertyAccessor() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public ExpressionParser getExpressionParser() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}		
}
