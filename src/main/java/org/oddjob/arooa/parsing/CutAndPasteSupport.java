package org.oddjob.arooa.parsing;

import org.oddjob.arooa.*;
import org.oddjob.arooa.deploy.BeanDescriptorHelper;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.xml.XMLArooaParser;

/**
 * Provide support for Cutting and Pasting from any form of parsed 
 * {@link ArooaConfiguration}.
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
	 * 
	 * @return The index the node was cut from.
	 */
	public static int cut(ParseContext<?> parentContext,
			ParseContext<?> childContext)
	throws ArooaConfigurationException {

		int index = parentContext.getConfigurationNode().indexOf(
				childContext.getConfigurationNode());

		childContext.destroy();

		return index;
	}
	
	/**
	 * Add any configuration to the parent context.
	 * 
	 * @param parentContext
	 * @param index
	 */
	public static ConfigurationHandle<ArooaContext> paste(ArooaContext parentContext, int index,
			ArooaConfiguration config) throws ArooaParseException {
		
		parentContext.getConfigurationNode().setInsertPosition(index);

		ConfigurationHandle<ArooaContext> handle;
		
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
	public static <P extends ParseContext<P>> ReplaceResult<P> replace(final P parentContext,
			final P childContext,
			ArooaConfiguration config) 
	throws ArooaParseException, ArooaConfigurationException {
		
		final int index = parentContext.getConfigurationNode().indexOf(
				childContext.getConfigurationNode());
		
		if (index < 0) {
			throw new IllegalStateException(
					"Attempting to cut a configuration node that is not a child of it's parent.");
		}
		
		XMLArooaParser xmlParser = new XMLArooaParser(parentContext.getPrefixMappings());
		ConfigurationHandle<P> rollbackHandle = xmlParser.parse(childContext.getConfigurationNode());
		
		childContext.destroy();
		
		parentContext.getConfigurationNode().setInsertPosition(index);
		
		ConfigurationHandle handle;
		ArooaParseException exception = null;
		
		try {
			handle = ParsingSession
                    .<ConfigurationHandle, ArooaParseException>doIn(
                            () -> config.parse(parentContext));
		}
		catch (Exception e) {

			try {
				// if parsing fails put back the original.
				handle = rollbackHandle.getDocumentContext()
                                       .getConfigurationNode()
                                       .parse(parentContext);

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
			
		return new ReplaceResult<>(handle, exception);
	}
	
	/**
	 * Result for replace.
	 */
	public static class ReplaceResult<P extends ParseContext<P>> {
		
		private final ConfigurationHandle<P> handle;
		
		private final ArooaParseException exception;
		
		
		public ReplaceResult(ConfigurationHandle<P> handle,
					ArooaParseException exception) {
			this.handle = handle;
			this.exception = exception;
		}
		
		public ArooaParseException getException() {
			return exception;
		}
		
		public ConfigurationHandle<P> getHandle() {
			return handle;
		}
	}
	
	public static String copy(ArooaContext context) {
		XMLArooaParser xmlParser = new XMLArooaParser(context.getPrefixMappings());
		
		try {
			xmlParser.parse(context.getConfigurationNode());
		}
		catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}
		return xmlParser.getXml();
	}

}

