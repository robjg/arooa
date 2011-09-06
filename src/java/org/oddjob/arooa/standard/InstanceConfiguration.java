package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.TextHandler;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.AutoSetter;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;

/**
 * Holds configuration information for an instance of 
 * something (a component or value).
 */
abstract class InstanceConfiguration {

	private final ArooaClass arooaClass;
	
    /** The object to configure. */
    private final Object wrappedObject;

    private final AttributeSetter attributeSetter;
    
    private final TextHandler textHandler = new TextHandler();
    
	private final String id;
	
    private final AutoSetter autoSetter;
	
    
    interface InjectionStrategy {
    	void init(ParentPropertySetter parentPropertySetter)
    	throws ArooaPropertyException;
    	void configure(ParentPropertySetter parentPropertySetter)
    	throws ArooaPropertyException;
    	void destroy(ParentPropertySetter parentPropertySetter)
    	throws ArooaPropertyException;
    }
    
    /**
     * Constructor for creating a wrapper for the specified object.
     * <p>
     * 
     * @param wrappedObject The element to configure. Must not be <code>null</code>.
     * @param parentProperty The tag name of the parent property <code>null</code>.
     * @param context A Context, may not be null.
     */
    InstanceConfiguration(ArooaClass arooaClass, 
    		Object wrappedObject, ArooaAttributes attributes) {
    	if (arooaClass == null) {
    		throw new NullPointerException("No ArooaClass.");
    	}
    	if (wrappedObject == null) {
    		throw new NullPointerException("No Wrapped Object.");
    	}
    	this.arooaClass = arooaClass;
        this.wrappedObject = wrappedObject;
        attributeSetter = new AttributeSetter(
        		this, attributes);
		this.id = attributes.get(
				ArooaConstants.ID_PROPERTY);
		
		getAttributeSetter().addOptionalAttribute(
				ArooaConstants.ID_PROPERTY);
        
        this.autoSetter = new AutoSetter();

    }

    String getId() {
    	return id;
    }
    
    Object getWrappedObject() {
    	return wrappedObject;
    }
    
    ArooaClass getArooaClass() {
    	return arooaClass;
    }
    
    abstract InjectionStrategy injectionStrategy();
    
    abstract Object getObjectToSet();

    AttributeSetter getAttributeSetter() {
    	return attributeSetter;
    }
    
    public void addText(String text) throws ArooaException {
        textHandler.addText(text);
    }    	
    
    String getText() {
    	
        return textHandler.getText();
    }

    void setProperty(
    		String name, Object value, ArooaContext context) 
    throws ArooaPropertyException {
    	PropertyAccessor propertyAccessor = 
    			context.getSession().getTools().getPropertyAccessor();
    	propertyAccessor.setSimpleProperty(wrappedObject, name, value);
    	autoSetter.markAsSet(name);
    }
    
    void setMappedProperty(
    		String name, String key, Object value, ArooaContext context) 
    throws ArooaPropertyException {
    	PropertyAccessor propertyAccessor = 
			context.getSession().getTools().getPropertyAccessor();
    	propertyAccessor.setMappedProperty(wrappedObject, name, key, value);
    }

    void setIndexedProperty(
    		String name, int index, Object value, ArooaContext context) 
    throws ArooaPropertyException {
    	PropertyAccessor propertyAccessor = 
			context.getSession().getTools().getPropertyAccessor();
    	propertyAccessor.setIndexedProperty(wrappedObject, name, index, value);
    }

    void setTextProperty(String text, ArooaSession session) 
    throws ArooaPropertyException {
    	
        PropertyAccessor propertyAccessor = session.getTools(
        	).getPropertyAccessor();

    	ArooaBeanDescriptor beanDescriptor = 
            	session.getArooaDescriptor(
            	).getBeanDescriptor(
            			arooaClass, propertyAccessor);    		

    	String textProperty = new BeanDescriptorHelper(
    			beanDescriptor).getTextProperty();
    	
        if (textProperty == null) {
        	throw new ArooaException("No text property for " +
        			wrappedObject.getClass().getName() +
        			" for text [" + text + "]");
        }    	
        
        propertyAccessor.setSimpleProperty(wrappedObject, 
        		textProperty, text);
    }
    
    
    final void internalInit(ArooaContext context) 
    throws ArooaPropertyException {

    	attributeSetter.init(context);

		String text = getText();
		if (text != null) {
	    	ArooaSession session = context.getSession();
	    	
			ExpressionParser expressionParser = session.getTools().getExpressionParser();
			ParsedExpression evaluator = expressionParser.parse(text);

			if (evaluator.isConstantText()) {
				setTextProperty(text, session);
			}
		}

    }
    
    final void internalConfigure(ArooaContext context) 
    throws ArooaConfigurationException {
    	
    	// configure attributes
    	attributeSetter.configure(context);
		
		String text = getText();
		if (text != null) {
				
	    	ArooaSession session = context.getSession();
	    	
			ExpressionParser expressionParser = session.getTools().getExpressionParser();
			ParsedExpression evaluator = expressionParser.parse(text);

	    	if (!evaluator.isConstantText()) {
			
				try {
					String replacement = evaluator.evaluateAsText(session);

					setTextProperty(replacement,
							session);
				}
				catch (ArooaConversionException e) {
					throw new ArooaPropertyException(
							"Text Property", e);
				}
	    	}
		}

		autoSetter.setServices(context);
    }
  
    final void internalDestroy(ArooaContext context) 
    throws ArooaConfigurationException {
    	// here for symmetry only (as yet).
    }
    
	abstract void configure(InstanceRuntime ourWrapper,
			ArooaContext context) 
	throws ArooaConfigurationException;

	abstract void init(InstanceRuntime ourWrapper,
			ArooaContext context) 
	throws ArooaConfigurationException;

	abstract void destroy(InstanceRuntime ourWrapper,
			ArooaContext context) 
	throws ArooaConfigurationException;
	
	abstract void listenerConfigure(InstanceRuntime ourWrapper,
			ArooaContext context) 
	throws ArooaConfigurationException;

	abstract void listenerDestroy(InstanceRuntime ourWrapper,
			ArooaContext context) 
	throws ArooaConfigurationException;
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + wrappedObject;
	}
	
}
