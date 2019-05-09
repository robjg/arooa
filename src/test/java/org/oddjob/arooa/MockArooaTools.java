package org.oddjob.arooa;

import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ServiceHelper;
import org.oddjob.arooa.runtime.Evaluator;
import org.oddjob.arooa.runtime.ExpressionParser;

public class MockArooaTools implements ArooaTools {

	@Override
	public ArooaConverter getArooaConverter() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public PropertyAccessor getPropertyAccessor() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public ExpressionParser getExpressionParser() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}		
	
	@Override
	public Evaluator getEvaluator() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public Evaluator getScriptEvaluator() {
		throw new RuntimeException("Unexpected from class: " +
				this.getClass().getName());
	}

	@Override
	public ServiceHelper getServiceHelper() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
