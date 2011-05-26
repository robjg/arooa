package org.oddjob.arooa.parsing.interceptors;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.RuntimeListener;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;

/**
 * An interceptor that allows all child elements to contribute to the
 * configuraiton of a single property.
 * <p>
 * This is typically where a component only has one property which
 * is a value or component. The configuration is more readable because
 * the property name element can be ommitted. For instance:
 * 
 * <pre>
 * &lt;snack&gt;
 *   &lt;fruit&gt;
 *     &lt;apple/&gt;
 *   &lt;/fruit&gt;
 * &lt;snack&gt;
 * </pre>
 * becomes:
 * <pre>
 * &lt;snack&gt;
 *   &lt;apple/&gt;
 * &lt;list&gt;
 * </pre>
 * 
 * This interceptor works by creating an invisible RuntimeConfiguration
 * equivalent to the omitted element.
 * 
 * 
 * @author rob
 *
 */
public class OnePropertyInterceptor 
implements ParsingInterceptor {
	
	private String property;

	class ChainedRuntime implements RuntimeConfiguration {
		
		private final RuntimeConfiguration originalRuntime;
		
		/** The container runtime created for the property. */
		private final RuntimeConfiguration propertyRuntime;
		
		public ChainedRuntime(RuntimeConfiguration originalRuntime,
				RuntimeConfiguration propertyRuntime) {
			this.originalRuntime = originalRuntime;
			this.propertyRuntime = propertyRuntime;
		}
		
		public void addRuntimeListener(
				RuntimeListener listener) {
			propertyRuntime.addRuntimeListener(listener);
		}

		public void init() throws ArooaConfigurationException {
			propertyRuntime.init();
			originalRuntime.init();
		}
		
		public void configure() throws ArooaConfigurationException {
			// Called after parsing so act on the parent.
			originalRuntime.configure();
		}

		public void destroy() throws ArooaConfigurationException {
			// Called after parsing so act on the parent.
			originalRuntime.destroy();
		}

		public ArooaClass getClassIdentifier() {
			return propertyRuntime.getClassIdentifier();
		}

		public void removeRuntimeListener(
				RuntimeListener listener) {
			propertyRuntime.removeRuntimeListener(listener);
		}

		public void setIndexedProperty(String name, int index,
				Object value) throws ArooaPropertyException {
			propertyRuntime.setIndexedProperty(name, index, value);
			
		}

		public void setMappedProperty(String name, String key,
				Object value) throws ArooaPropertyException {
			propertyRuntime.setMappedProperty(name, key, value);
		}

		public void setProperty(String name, Object value)
				throws ArooaPropertyException {
			propertyRuntime.setProperty(name, value);
		}
	}
	
	/** 
	 * The sole purpose of this is to pass through the 
	 * init method so both runtime get inited.
	 */
	class ContextImposter implements ArooaContext {
		private final ArooaContext originalContext;
		private final ArooaContext interceptorContext;

		ContextImposter(ArooaContext originalContext, 
				ArooaContext interceptorContext) {
			this.originalContext = originalContext;
			this.interceptorContext = interceptorContext;
		}

		public ArooaType getArooaType() {
			return interceptorContext.getArooaType();
		}
		
		public ArooaContext getParent() {
			return originalContext.getParent();
		}
		
		public ArooaHandler getArooaHandler() {
			return interceptorContext.getArooaHandler();
		}

		public PrefixMappings getPrefixMappings() {
			return interceptorContext.getPrefixMappings();
		}

		public RuntimeConfiguration getRuntime() {
			return new ChainedRuntime(
					originalContext.getRuntime(),
					interceptorContext.getRuntime());
		}

		public ConfigurationNode getConfigurationNode() {
			// intercepter context should not change the node.
			return originalContext.getConfigurationNode();
		}

		public ArooaSession getSession() {
			return interceptorContext.getSession();
		}
		
	}
	
	public OnePropertyInterceptor() {
	}
	
	public OnePropertyInterceptor(String property) {
		this.property = property; 
	}
	
	public ArooaContext intercept(final ArooaContext suggestedContext) 
	throws ArooaConfigurationException {
		
		if (property == null) {
			throw new ArooaException("Property attribute must be provided for an AllElmenetsOnePropertyInterceptor.");
		}

		ArooaContext propertyContext = suggestedContext.getArooaHandler(
			).onStartElement(
					new ArooaElement(property), 
					suggestedContext);

		return new ContextImposter(suggestedContext,
				propertyContext);
	}
	

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
