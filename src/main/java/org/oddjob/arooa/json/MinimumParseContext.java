package org.oddjob.arooa.json;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.xml.XMLConfigurationNode;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Helper for generating the minimal context necessary to parse an ArooaConfiguration into a
 * simple form such as JSON.
 */
public class MinimumParseContext {

    public static ArooaContext createRootContext(ArooaHandler arooaHandler) {

        return new RootContext(null, (ArooaSession) null, arooaHandler);
    }

    public static class Options {

        private Runnable initCallback;

        private Consumer<String> textCallback;

        private ArooaHandler childHandler;

        public Options initCallback(Runnable initCallback) {
            this.initCallback = initCallback;
            return this;
        }

        public Options textCallback(Consumer<String> textCallback) {
            this.textCallback = textCallback;
            return this;
        }

        public Options childHandler(ArooaHandler handler) {
            this.childHandler = handler;
            return this;
        }

        public ArooaContext createChild(ArooaElement element,
                                        ArooaContext parentContext) {
            return MinimumParseContext.createChild(element, parentContext, this);
        }
    }

    public static Options withOptions() {
        return new Options();
    }

    public static ArooaContext createChild(ArooaElement element,
                                           ArooaContext parentContext) {

        return createChild(element, parentContext, new Options());
    }

    public static ArooaContext createChild(ArooaElement element,
                                           ArooaContext parentContext,
                                           Options options) {

        XMLConfigurationNode configurationNode = new XMLConfigurationNode(element);
        MinimumRuntime runtime = new MinimumRuntime(
                () -> {
                    Optional.ofNullable(options.textCallback)
                            .ifPresent(tc -> tc.accept(
                                    configurationNode.getText()));
                    Optional.ofNullable(options.initCallback)
                            .ifPresent(Runnable::run);
                }
        );

        MinimumContext context = new MinimumContext(parentContext,
                runtime,
                configurationNode,
                Optional.ofNullable(options.childHandler)
                        .orElseGet(parentContext::getArooaHandler));
        configurationNode.setContext(context);
        return context;
    }

    static class MinimumContext implements ArooaContext {

        private final RuntimeConfiguration runtimeConfiguration;

        private final ArooaContext parent;

        private final ConfigurationNode<ArooaContext> configurationNode;

        private final ArooaHandler arooaHandler;

        public MinimumContext(ArooaContext parent,
                              RuntimeConfiguration runtimeConfiguration,
                              ConfigurationNode<ArooaContext> configurationNode,
                              ArooaHandler arooaHandler) {
            this.runtimeConfiguration = runtimeConfiguration;
            this.parent = parent;
            this.configurationNode = configurationNode;
            this.arooaHandler = arooaHandler;
        }

        @Override
        public ArooaType getArooaType() {
            return null;
        }

        @Override
        public ArooaContext getParent() {
            return parent;
        }

        @Override
        public RuntimeConfiguration getRuntime() {
            return runtimeConfiguration;
        }

        @Override
        public ConfigurationNode<ArooaContext> getConfigurationNode() {
            return configurationNode;
        }

        @Override
        public ArooaHandler getArooaHandler() {
            return arooaHandler;
        }

        @Override
        public PrefixMappings getPrefixMappings() {
            return parent.getPrefixMappings();
        }

        @Override
        public ArooaSession getSession() {
            throw new UnsupportedOperationException();
        }

    }

    static class MinimumRuntime extends AbstractRuntimeConfiguration {

        private final Runnable initCallback;

        MinimumRuntime(Runnable initCallback) {
            this.initCallback = initCallback;
        }

        public ArooaClass getClassIdentifier() {
            throw new UnsupportedOperationException();
        }

        public void configure()
                throws ArooaConfigurationException {
            fireBeforeConfigure();
            fireAfterConfigure();
        }

        public void init()
                throws ArooaConfigurationException {
            fireBeforeInit();
            Optional.ofNullable(initCallback).ifPresent(Runnable::run);
            fireAfterInit();
        }

        public void destroy()
                throws ArooaConfigurationException {
            fireBeforeDestroy();
            fireAfterDestroy();
        }

        public void setIndexedProperty(String name, int index, Object value) {
            throw new UnsupportedOperationException();
        }

        public void setMappedProperty(String name, String key, Object value) {
            throw new UnsupportedOperationException();
        }

        public void setProperty(String name, Object value)
                throws ArooaException {
            throw new UnsupportedOperationException();
        }
    }

}
