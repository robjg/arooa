package org.oddjob.arooa.reflect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * Provides a result based on a class definition, descriptor and element definition. 
 * 
 * 
 * @author rob
 *
 * @param <R>
 */
public class PropertyIdentifier<R, N> {

	private static final Logger logger = LoggerFactory.getLogger(
			PropertyIdentifier.class);
	
	/**
	 * Element Action Factory
	 * 
	 */
	public interface ElementActionFactory<N> {
		
		ElementAction<N> createComponentElementAction();
		
		ElementAction<N> createValueElementAction();

	}
	
	/**
	 * Users of the PropertyIdentifier class provide an instance of this to provide the correct Result in
	 * the various possibilities for the property.
	 * 
	 * @param <R>
	 */
	public interface PropertyTypeActions<R, N> {
		
		R onMappedElement(ArooaElement element,
				ArooaContext context, ElementAction<N> action)
		throws ArooaPropertyException;
		
		R onIndexedElement(ArooaElement element,
				ArooaContext context, ElementAction<N> action)
		throws ArooaPropertyException;
		
		R onVariantElement(ArooaElement element,
				ArooaContext context, ElementAction<N> action)
		throws ArooaPropertyException;
	}

	private final ElementActionFactory<N> actionFactory; 
	
	/** The element action for factory types. */
	private final PropertyTypeActions<R, N> elementAction;
	
	/**
	 * Constructor.
	 * 
	 * @param elementAction
	 */
	public PropertyIdentifier(ElementActionFactory<N> actionFactory, 
			PropertyTypeActions<R, N> elementAction) {
		this.actionFactory = actionFactory;
		this.elementAction = elementAction;
	}

    /**
     *
     * @param classId
     * @param element
     * @param context

     * @return

     * @throws ArooaConfigurationException
     */
	public R identifyPropertyFor(ArooaClass classId,
		ArooaElement element, ArooaContext context) 
	throws ArooaConfigurationException {
		
		String propertyName = element.getTag();

		ElementAction<N> childAction = new ElementAction<N>() {
			public N onElement(ArooaElement element, ArooaContext context) {
				if (context.getArooaType() == ArooaType.COMPONENT) {
					return actionFactory.createComponentElementAction(
							).onElement(element, context);
				}
				else {
					return actionFactory.createValueElementAction(
							).onElement(element, context);		
				}
			}
		};
		
		
		String debug = "Property [" + propertyName + "]";
		
		PropertyAccessor propertyAccessor = context.getSession().getTools(
			).getPropertyAccessor();

		BeanOverview beanOverview = classId.getBeanOverview(
				propertyAccessor);

		R result;
		// now check if it's a list or a map 
		if (beanOverview.isIndexed(propertyName)) {
			result = elementAction.onIndexedElement(element, context, childAction);
			debug += " (indexed)";
		}
		else if (beanOverview.isMapped(propertyName)) {
			result = elementAction.onMappedElement(element, context, childAction);
			debug += " (mapped)";
		}
		else { 
			result = elementAction.onVariantElement(element, context, childAction);
		}
		
		logger.debug(debug);
		return result;
	}


}
