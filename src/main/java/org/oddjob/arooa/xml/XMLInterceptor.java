package org.oddjob.arooa.xml;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.HandlerOverrideContext;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

import java.util.Optional;

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

    public ArooaContext intercept(ArooaContext interceptContext) {

        final RuntimeConfiguration interceptRuntime = interceptContext.getRuntime();

        final XmlHandler2 actualHandler = new XmlHandler2();

        RuntimeListener runtimeListener = new RuntimeListener() {
            public void beforeInit(RuntimeEvent event) throws
                    ArooaConfigurationException {
            }
            public void afterInit(RuntimeEvent event)
                    throws ArooaConfigurationException {
                Optional.ofNullable(property)
                        .ifPresent(p ->
                                interceptRuntime.setProperty(p,
                                        actualHandler.getXml()));
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
                Optional.ofNullable(property)
                        .ifPresent(p ->
                                interceptRuntime.setProperty(p, null));
            }
        };

        final ArooaHandler handlerAdapter = (element, parentContext) -> {
            ArooaContext firstElementContext = actualHandler.onStartElement(element, parentContext);
            firstElementContext.getRuntime().addRuntimeListener(runtimeListener);
            return firstElementContext;
        };

        return new HandlerOverrideContext(
                interceptContext,
                handlerAdapter);
    }

}


