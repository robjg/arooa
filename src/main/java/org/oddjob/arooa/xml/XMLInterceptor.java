package org.oddjob.arooa.xml;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.HandlerOverrideContext;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

/**
 * This handler converts the events back into XML.
 */
public class XMLInterceptor implements ParsingInterceptor {
	
	private String property;
		
	public XMLInterceptor() {
	}
	
	public XMLInterceptor(String property) {
		this.property = property;
	}
	
	
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public ArooaContext intercept(ArooaContext context) {
		final RuntimeConfiguration parentRuntime = context.getRuntime();
		
		final XmlHandler2 actualHandler = new XmlHandler2();
		
		parentRuntime.addRuntimeListener(
				new RuntimeListener() {
					public void beforeInit(RuntimeEvent event) throws 
					ArooaConfigurationException {
						parentRuntime.setProperty(property, 
								actualHandler.getXml());
					}
					
					public void afterInit(RuntimeEvent event)
					throws ArooaConfigurationException {
					}
					
					public void beforeConfigure(RuntimeEvent event)
					throws ArooaConfigurationException {
					}
					
					public void afterConfigure(RuntimeEvent event) 
					throws ArooaConfigurationException {
					}
					
					public void beforeDestroy(RuntimeEvent event)
					throws ArooaConfigurationException {
					}
					
					public void afterDestroy(RuntimeEvent event) 
					throws ArooaConfigurationException {
						parentRuntime.setProperty(property, null);
					}

		});

		return new HandlerOverrideContext(
				context,
				actualHandler);
	}
	
}


