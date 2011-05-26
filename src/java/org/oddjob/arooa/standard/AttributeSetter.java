/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.standard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaNoPropertyException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.oddjob.arooa.types.BeanType;

/**
 * A helper class that collects optional attributes and takes
 * these optional attributes into account when setting the
 * attributes of the runtime.
 * <p>
 * An example of an optional attribute is 'id'.
 * 
 * @author rob
 *
 */
class AttributeSetter {

	private final Set<String> optionalAttributes = new HashSet<String>();
	
    private final InstanceConfiguration instance;
    
    private final ArooaAttributes attributes;
    
    AttributeSetter(InstanceConfiguration instance, ArooaAttributes attributes) {
    	this.instance = instance;
		this.attributes = attributes;
    }
	
	public void addOptionalAttribute(String name) {
		optionalAttributes.add(name);
	}
	
	List<AttributeRuntime> runtimes(
			ArooaContext context)
	throws ArooaException {

	    List<AttributeRuntime> attributeRuntimes = 
	    	new ArrayList<AttributeRuntime>();
	    
		MutableAttributes attrs = new MutableAttributes(attributes);
				
		ArooaClass classIdentifier = 
			context.getRuntime().getClassIdentifier();
		
		ArooaSession session = context.getSession();
		
		PropertyAccessor propertyAcessor = 
			session.getTools().getPropertyAccessor(); 
		
		BeanOverview beanOverview = classIdentifier.getBeanOverview(
				propertyAcessor);
		
		for (String name : optionalAttributes) {
			String value = attrs.get(name);
			
			if (value == null) {
				continue;
			}
			
			if (!beanOverview.hasWriteableProperty(name)) {
				attrs.remove(name);
			}
		}

		Class<?> objectClass = instance.getWrappedObject().getClass();
			
		ArooaBeanDescriptor beanDescriptor = 
			session.getArooaDescriptor().getBeanDescriptor(
				classIdentifier, propertyAcessor);
		
		for (String propertyName : attrs.getAttributNames()) {
			
			// special handling for 'class' attribute.
			if (BeanType.ATTRIBUTE.equals(propertyName)) {
				continue;
			}
			
			if (!beanOverview.hasWriteableProperty(propertyName)) {
				throw new ArooaNoPropertyException(propertyName, 
						classIdentifier.forClass());
			}
			
			ConfiguredHow configuredHow = new BeanDescriptorHelper(
					beanDescriptor).getConfiguredHow(propertyName);
			if (ConfiguredHow.ATTRIBUTE != configuredHow) {
				throw new ArooaConfigurationException(propertyName + 
						" is not configured as an ATTRIBUTE but as (an) " + 
						configuredHow + " of " + objectClass);
			}
			
			ExpressionParser expressionParser = context.getSession(
					).getTools().getExpressionParser();
			ParsedExpression evaluator = expressionParser.parse(
					attrs.get(propertyName));
	
			attributeRuntimes.add(
					new AttributeRuntime(
							instance, 
							propertyName, 
							evaluator,
							beanOverview.getPropertyType(propertyName)));
		}
		
		return attributeRuntimes;
	}
	
	void init(ArooaContext context) 
	throws ArooaPropertyException {

		for (AttributeRuntime ar: runtimes(context)) {
			ar.init(context);
		}
	}
	
	void configure(ArooaContext context) 
	throws ArooaPropertyException {

		for (AttributeRuntime ar: runtimes(context)) {
			ar.configure(context);
		}
	}
}
