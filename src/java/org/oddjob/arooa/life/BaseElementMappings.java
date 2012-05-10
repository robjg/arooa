package org.oddjob.arooa.life;

import java.util.ArrayList;
import java.util.Collection;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.beandocs.MappingsContents;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.types.BeanType;
import org.oddjob.arooa.types.BeanTypeDesFa;
import org.oddjob.arooa.types.IsType;

/**
 * The most basic {@link ElementMappings} that handles
 * <code>&lt;class&gt;</code> and <code>&lt;is&gt;</code>.
 * 
 * @author rob
 *
 */
public class BaseElementMappings implements ElementMappings {

	
	@Override
	public ArooaClass mappingFor(final ArooaElement element,
			final InstantiationContext propertyContext) {
		
		if (IsType.ELEMENT.equals(element)) {
								
			ArooaClass arooaClass = propertyContext.getArooaClass();
			if (arooaClass == null) {
				throw new ArooaException("Element [" + element + 
				"] can not be the root element because the class is not known.");
			}
			return arooaClass;
		}
		if (BeanType.ELEMENT.equals(element)) {
			
			String className = element.getAttributes().get(BeanType.ATTRIBUTE);
			if (className == null) {
				className = Object.class.getName();
			}
			SimpleArooaClass classIdentifier = new SimpleArooaClass(
					forName(className, propertyContext));

			return classIdentifier;					
		}
		
		return null;
	}
	
	private Class<?> forName(String className, InstantiationContext context) {
		
		ClassResolver classResolver = context.getClassResolver();

		Class<?> theClass = classResolver.findClass(className);
		
		if (theClass == null) {
			throw new ArooaException("Can't find class " + className);
		}
		
		return theClass;
	}

	@Override
	public DesignFactory designFor(ArooaElement element, 
			InstantiationContext parentContext) {
		
		if (BeanType.ELEMENT.equals(element)) {
			return new BeanTypeDesFa();
		}
		
		if (IsType.ELEMENT.equals(element)) {
			
			return new GenericDesignFactory(
							parentContext.getArooaClass());
		}
		
		return null;
	}
	
	@Override
	public ArooaElement[] elementsFor(InstantiationContext propertyContext) {
		
		Collection<ArooaElement> supports = new ArrayList<ArooaElement>();

		if (IsType.supports(propertyContext)) {
			supports.add(IsType.ELEMENT);
		}

		supports.add(BeanType.ELEMENT);
		
		return supports.toArray(new ArooaElement[supports.size()]);
	}

	@Override
	public MappingsContents getBeanDoc(ArooaType arooaType) {
		return new MappingsContents() {
			@Override
			public ArooaElement[] allElements() {
				return new ArooaElement[] {
						IsType.ELEMENT, BeanType.ELEMENT };
			}
			@Override
			public ArooaClass documentClass(ArooaElement element) {
				if (IsType.ELEMENT.equals(element)) {
					return new SimpleArooaClass(IsType.class);
				}
				if (BeanType.ELEMENT.equals(element)) {
					return new SimpleArooaClass(BeanType.class);
				}
				return null;
			}
		};
	}
}
