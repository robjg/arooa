package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.xml.XMLArooaParser;

/**
 * Provide support for Cutting and Pasting from any form of parsed 
 * {@link ArooaConfiguraion}.
 * 
 * @author rob
 *
 */
public class CutAndPasteSupport {

	/** The context of the component instance. */
	private final ArooaContext instanceContext;
	
	/** The component property name. */
	private final String propertyName;
	
	/**
	 * Constructor.
	 * 
	 * @param instanceContext The context of the component we are providing
	 * the support for.
	 */
	public CutAndPasteSupport(ArooaContext instanceContext) {
		this.instanceContext = instanceContext;
		
		ArooaSession session = instanceContext.getSession(); 
		
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		ArooaClass runtimeClass = 
			instanceContext.getRuntime().getClassIdentifier(); 
		
		ArooaBeanDescriptor beanDescriptor = 
			session.getArooaDescriptor().getBeanDescriptor(
					runtimeClass, accessor);

		propertyName = new BeanDescriptorHelper(beanDescriptor).getComponentProperty();
	}
	
	/**
	 * Helper to find the property context of for the component property.
	 * 
	 * @return The context of the property element.
	 * 
	 * @throws ArooaConfigurationException
	 */
	private ArooaContext propertyContext() 
	throws ArooaConfigurationException {
		
		if (propertyName == null) {
			throw new UnsupportedOperationException("No component property."); 
		}
		
		ArooaContext propertyContext = instanceContext.getArooaHandler().onStartElement(
				new ArooaElement(propertyName), instanceContext);

		// The property context may already exist.
		if (instanceContext.getConfigurationNode().indexOf(
				propertyContext.getConfigurationNode()) < 0) {

			int propIndex = instanceContext.getConfigurationNode().insertChild(
					propertyContext.getConfigurationNode());

			try {
				propertyContext.getRuntime().init();
			} catch (ArooaConfigurationException e) {
				instanceContext.getConfigurationNode().removeChild(propIndex);
				throw e;
			}
		}		

		return propertyContext;
	}
	
	/**
	 * Does this instance support pasting.
	 * 
	 * @return
	 */
	public boolean supportsPaste() {
		return (propertyName != null);
	}	
	
	/**
	 * Remove the component who's context is given.
	 * 
	 * @param childContext
	 * @throws ArooaConfigurationException
	 */
	public void cut(ArooaContext childContext) 
	throws ArooaConfigurationException {
		
		if (propertyName == null) {
			throw new UnsupportedOperationException("No component property."); 
		}
		
		ArooaContext propertyContext = instanceContext.getArooaHandler().onStartElement(
					new ArooaElement(propertyName), instanceContext);

		if (instanceContext.getConfigurationNode().indexOf(
				propertyContext.getConfigurationNode()) < 0) {
			
			throw new IllegalStateException(
					"Context is not a child of the component property.");
		}
		
		cut(propertyContext, childContext);
	}

	/**
	 * Paste the {@link ArooaConfiguration}.
	 * 
	 * @param index
	 * @param config
	 * 
	 * @return
	 * 
	 * @throws ArooaParseException
	 * @throws ArooaConfigurationException
	 */
	public ConfigurationHandle paste(int index, 
			ArooaConfiguration config) 
	throws ArooaParseException, ArooaConfigurationException {
		
		return paste(propertyContext(), index, config);
	}
	
	/**
	 * Replace the childContext with the given configuration.
	 * 
	 * @param childContext
	 * @param config
	 * @return
	 * @throws ArooaParseException
	 * @throws ArooaConfigurationException
	 */
	public ReplaceResult replace(ArooaContext childContext,
			ArooaConfiguration config) 
	throws ArooaParseException, ArooaConfigurationException {
		
		if (propertyName == null) {
			throw new UnsupportedOperationException("No component property."); 
		}
		
		ArooaContext propertyContext = instanceContext.getArooaHandler().onStartElement(
					new ArooaElement(propertyName), instanceContext);

		if (instanceContext.getConfigurationNode().indexOf(
				propertyContext.getConfigurationNode()) < 0) {
			
			throw new IllegalStateException(
					"Context is not a child of the component property.");
		}

		return replace(propertyContext, childContext, config);
	}

	/**
	 * Cut when the parent context is known.
	 * 
	 * @param parentContext
	 * @param childContext
	 * @throws ArooaConfigurationException
	 */
	public static void cut(ArooaContext parentContext,
			ArooaContext childContext) 
	throws ArooaConfigurationException {
		
		int index = parentContext.getConfigurationNode().indexOf(
				childContext.getConfigurationNode());
		
		if (index < 0) {
			throw new IllegalStateException(
					"Attempting to cut a configuration node that is not a child of it's parent.");
		}
		
		childContext.getRuntime().destroy();
		
		parentContext.getConfigurationNode().removeChild(
				index);
	}
	
	/**
	 * Add any configuration to the parent context.
	 * 
	 * @param parentContext
	 * @param index
	 */
	public static ConfigurationHandle paste(ArooaContext parentContext, int index, 
			ArooaConfiguration config) throws ArooaParseException {
		
		parentContext.getConfigurationNode().setInsertPosition(index);

		ConfigurationHandle handle = null;
		
		handle = config.parse(parentContext);
			
		return handle;
	}

	/**
	 * Replaces a child context with the contents of the configuration.
	 * 
	 * @param parentContext
	 * @param childContext
	 * @param config
	 * @throws ArooaParseException
	 */
	public static ReplaceResult replace(final ArooaContext parentContext, 
			final ArooaContext childContext,
			ArooaConfiguration config) 
	throws ArooaParseException, ArooaConfigurationException {
		
		final int index = parentContext.getConfigurationNode().indexOf(
				childContext.getConfigurationNode());
		
		if (index < 0) {
			throw new IllegalStateException(
					"Attempting to cut a configuration node that is not a child of it's parent.");
		}
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		ConfigurationHandle rollbackHandle = xmlParser.parse(childContext.getConfigurationNode());
		
		childContext.getRuntime().destroy();
		
		parentContext.getConfigurationNode().removeChild(
				index);		
		
		parentContext.getConfigurationNode().setInsertPosition(index);
		
		ConfigurationHandle handle = null;
		ArooaParseException exception = null;
		
		try {
			handle = config.parse(parentContext);
		}
		catch (Exception e) {
			
			try {
				// if parsing fails put back the original.
				handle = rollbackHandle.getDocumentContext().getConfigurationNode().parse(parentContext);
				if (e instanceof ArooaParseException) {
					exception = (ArooaParseException) e;
				}
				else {
					exception = new ArooaParseException("Replace Failed.", null, e);
				}
			} 
			catch (ArooaParseException e2){
				throw e2;
			}
			catch (RuntimeException e2){
				throw e2;
			}
		}
			
		return new ReplaceResult(handle, exception);
	}
	
	/**
	 * Result for replace.
	 */
	public static class ReplaceResult {
		
		private final ConfigurationHandle handle;
		
		private final ArooaParseException exception;
		
		
		public ReplaceResult(ConfigurationHandle handle,
					ArooaParseException exception) {
			this.handle = handle;
			this.exception = exception;
		}
		
		public ArooaParseException getException() {
			return exception;
		}
		
		public ConfigurationHandle getHandle() {
			return handle;
		}
	}
	
	public static String copy(ArooaContext context) {
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		try {
			xmlParser.parse(context.getConfigurationNode());
		}
		catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}
		return xmlParser.getXml();
	}

}

