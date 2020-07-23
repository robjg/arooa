package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.runtime.ConfigurationNode;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A Simple Parse Context.
 */
public interface SimpleParseContext extends ParseContext<SimpleParseContext> {

    interface Actions {

        Consumer<? super String> getTextConsumer();

        Runnable getInitAction();

        Runnable getDestroyAction();

        ActionFunction getChildActions();
    }

    @FunctionalInterface
    interface ActionFunction {

        Actions onElement(ArooaElement element);
    }


    interface Callbacks {

        void onText(String text);

        void onInit();

        void onDestroy();

        CallbackFunction childCallbacks();
    }

    @FunctionalInterface
    interface CallbackFunction {

        Callbacks onElement(ArooaElement element);
    }

    static RootBuilder1 createRootContext() {

        return new RootBuilder1();
    }

    default ChildBuilder1 createChildFor(ArooaElement element) {

        return new ChildBuilder1(element, this);
    }

    static ActionsBuilder actions() {
        return new ActionsBuilder();
    }

    class ActionsBuilder {

        private Consumer<? super String> textConsumer;

        private Runnable initAction;

        private Runnable destroyAction;

        private ActionFunction childActions;

        public ActionsBuilder withTextConsumer(Consumer<? super String> textConsumer) {
            this.textConsumer = textConsumer;
            return this;
        }

        public ActionsBuilder withInitAction(Runnable initAction) {
            this.initAction = initAction;
            return this;
        }

        public ActionsBuilder withDestroyAction(Runnable destroyAction) {
            this.destroyAction = destroyAction;
            return this;
        }

        public ActionsBuilder withChildActions(ActionFunction childActions) {
            this.childActions = childActions;
            return this;
        }

        public Actions create() {
            return new ActionsImpl(this);
        }
    }

    class ActionsImpl implements Actions {

        private final Consumer<? super String> textConsumer;

        private final Runnable initAction;

        private final Runnable destroyAction;

        private final ActionFunction childActions;

        ActionsImpl(ActionsBuilder builder) {
            this.textConsumer = builder.textConsumer;
            this.initAction = builder.initAction;
            this.destroyAction = builder.destroyAction;
            this.childActions = builder.childActions;
        }

        @Override
        public Consumer<? super String> getTextConsumer() {
            return textConsumer;
        }

        @Override
        public Runnable getInitAction() {
            return initAction;
        }

        @Override
        public Runnable getDestroyAction() {
            return destroyAction;
        }

        @Override
        public ActionFunction getChildActions() {
            return childActions;
        }
    }

    class ActionsCallbacks implements Callbacks {

        private final Actions actions;

        public ActionsCallbacks(Actions actions) {
            this.actions = actions;
        }

        @Override
        public void onText(String text) {
            Optional.ofNullable(actions.getTextConsumer())
                    .ifPresent(consumer -> consumer.accept(text));
        }

        @Override
        public void onInit() {
            Optional.ofNullable(actions.getInitAction())
                    .ifPresent(Runnable::run);
        }

        @Override
        public void onDestroy() {
            Optional.ofNullable(actions.getDestroyAction())
                    .ifPresent(Runnable::run);
        }

        @Override
        public CallbackFunction childCallbacks() {
            return Optional.ofNullable(actions.getChildActions())
                    .map(actionFunction ->
                            (CallbackFunction) element ->
                                    new ActionsCallbacks(actionFunction.onElement(element)))
                    .orElse(null);
        }
    }


    class CallbackElementHandler implements ElementHandler<SimpleParseContext> {

        private final CallbackFunction callbackFunction;

        public CallbackElementHandler(CallbackFunction callbackFunction) {
            this.callbackFunction = callbackFunction;
        }

        @Override
        public ParseHandle<SimpleParseContext> onStartElement(ArooaElement element,
                                                              SimpleParseContext parentContext)
                throws ArooaConfigurationException {

            Callbacks callbacks = callbackFunction.onElement(element);

            SimpleParseContext childContext = parentContext.createChildFor(element)
                    .withCallbackFunction(Optional.ofNullable(callbacks.childCallbacks())
                            .orElse(callbackFunction))
                    .andDestroyAction(callbacks::onDestroy);

            return new ParseHandle<SimpleParseContext>() {
                @Override
                public SimpleParseContext getContext() {
                    return childContext;
                }

                @Override
                public void addText(String text) {
                    childContext.getConfigurationNode().addText(text);
                }

                @Override
                public int init() {

                    String text = ((SimpleConfigNode) childContext.getConfigurationNode()).textHandler.getText();
                    if (text != null) {
                        callbacks.onText(text);
                    }

                    // order is important here:
                    // add node before init() so indexed properties
                    // know their index.

                    int index = parentContext.getConfigurationNode()
                            .insertChild(childContext.getConfigurationNode());

                    try {
                        callbacks.onInit();
                    } catch (RuntimeException e) {
                        parentContext.getConfigurationNode().removeChild(index);
                        throw e;
                    }

                    return index;
                }
            };
        }
    }

    class RootBuilder1 {

        public RootBuilder2 withElementHandler(ElementHandler<SimpleParseContext> elementHandler) {
            return new RootBuilder2(elementHandler);
        }

        public RootBuilder2 withCallbacks(CallbackFunction callbackFunction) {
            return withElementHandler(new CallbackElementHandler(callbackFunction));
        }

        public RootBuilder2 withActions(ActionFunction actionFunction) {
            return withCallbacks(element -> new ActionsCallbacks(actionFunction.onElement(element)));
        }

        public RootBuilder2 withNoActions() {
            return withActions(element -> actions().create());
        }
    }

    class RootBuilder2 {

        private final ElementHandler<SimpleParseContext> elementHandler;

        public RootBuilder2(ElementHandler<SimpleParseContext> elementHandler) {
            this.elementHandler = Objects.requireNonNull(elementHandler);
        }

        public SimpleParseContext andPrefixMappings(PrefixMappings prefixMappings) {
            return new SimpleRoot(elementHandler, prefixMappings);
        }

        public SimpleParseContext andNamespaceMappings(NamespaceMappings namespaceMappings) {
            return andPrefixMappings(new FallbackPrefixMappings(namespaceMappings));
        }

        public SimpleParseContext andNoPrefixMappings() {
            return andNamespaceMappings(NamespaceMappings.empty());
        }
    }

    class ChildBuilder1 {

        private final ArooaElement element;

        private final SimpleParseContext parent;

        public ChildBuilder1(ArooaElement element, SimpleParseContext parent) {
            this.element = element;
            this.parent = parent;
        }

        public ChildBuilder2 withActionFunction(ActionFunction actionFunction) {
            return withCallbackFunction(element -> new ActionsCallbacks(actionFunction.onElement(element)));
        }

        public ChildBuilder2 withCallbackFunction(CallbackFunction callbackFunction) {
            return withElementHandler(new CallbackElementHandler(callbackFunction));
        }

        public ChildBuilder2 withElementHandler(ElementHandler<SimpleParseContext> elementHandler) {
            return new ChildBuilder2(element, parent, elementHandler);
        }

    }

    class ChildBuilder2 {

        private final ArooaElement arooaElement;

        private final SimpleParseContext parent;

        private final ElementHandler<SimpleParseContext> elementHandler;

        public ChildBuilder2(ArooaElement arooaElement,
                             SimpleParseContext parent,
                             ElementHandler<SimpleParseContext> elementHandler) {
            this.arooaElement = arooaElement;
            this.parent = parent;
            this.elementHandler = elementHandler;
        }

        public SimpleParseContext andDestroyAction(Runnable destroyAction) {
            return new SimpleImpl(arooaElement, parent, elementHandler, destroyAction);
        }

        public SimpleParseContext andNoDestroyAction() {
            return new SimpleImpl(arooaElement, parent, elementHandler, () -> {
            });
        }
    }

    class SimpleRoot implements SimpleParseContext {

        private final ElementHandler<SimpleParseContext> elementHandler;

        private final PrefixMappings prefixMappings;

        private final ConfigurationNode<SimpleParseContext> configurationNode =
                new AbstractConfigurationNode<SimpleParseContext>() {

                    @Override
                    public SimpleParseContext getContext() {
                        return SimpleRoot.this;
                    }

                    @Override
                    public void addText(String text) {
                        throw new UnsupportedOperationException(
                                "It should be impossible to add text to the Configuration Node of the Root Context.");
                    }

                    @Override
                    public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext) {
                        throw new UnsupportedOperationException(
                                "It should be impossible to attempt to parse the Configuration Node of the Root Context");
                    }
                };

        public SimpleRoot(ElementHandler<SimpleParseContext> elementHandler, PrefixMappings prefixMappings) {
            this.elementHandler = Objects.requireNonNull(elementHandler);
            this.prefixMappings = Objects.requireNonNull(prefixMappings);
        }


        @Override
        public SimpleParseContext getParent() {
            return null;
        }

        @Override
        public ElementHandler<SimpleParseContext> getElementHandler() {
            return elementHandler;
        }

        @Override
        public PrefixMappings getPrefixMappings() {
            return prefixMappings;
        }

        @Override
        public ConfigurationNode<SimpleParseContext> getConfigurationNode() {
            return configurationNode;
        }

        @Override
        public void destroy() {
            throw new UnsupportedOperationException(
                    "It should be impossible to attempt to destroy the Root Context");
        }
    }


    class SimpleImpl implements SimpleParseContext {

        private final SimpleParseContext parent;

        private final ElementHandler<SimpleParseContext> elementHandler;

        private final SimpleConfigNode configurationNode;

        private final Runnable destroyAction;

        SimpleImpl(ArooaElement element,
                   SimpleParseContext parent,
                   ElementHandler<SimpleParseContext> elementHandler, Runnable destroyAction) {
            this.parent = Objects.requireNonNull(parent);
            this.elementHandler = Objects.requireNonNull(elementHandler);
            this.configurationNode = new SimpleConfigNode(element, this);
            this.destroyAction = Objects.requireNonNull(destroyAction);
        }

        @Override
        public SimpleParseContext getParent() {
            return parent;
        }

        @Override
        public ElementHandler<SimpleParseContext> getElementHandler() {
            return elementHandler;
        }

        @Override
        public PrefixMappings getPrefixMappings() {
            return parent.getPrefixMappings();
        }

        @Override
        public ConfigurationNode<SimpleParseContext> getConfigurationNode() {
            return configurationNode;
        }

        @Override
        public void destroy() {
            SimpleParseContext parentContext = this.getParent();

            int index = parentContext.getConfigurationNode().indexOf(
                    this.getConfigurationNode());

            if (index < 0) {
                throw new IllegalStateException(
                        "Attempting to destroy a configuration node that is not a child of it's parent.");
            }

            destroyAction.run();

            parentContext.getConfigurationNode().removeChild(
                    index);
        }
    }

    class SimpleConfigNode extends AbstractConfigurationNode<SimpleParseContext> {

        /**
         * The element
         */
        private final ArooaElement element;

        /**
         * Text appearing within the element.
         */
        private final TextHandler textHandler = new TextHandler();

        /**
         * The context that owns this configuration node. It can't be set in the constructor
         * because there is a chicken and egg situation between this and the context. It
         * is set only once after the context has been created with this
         */
        private final SimpleParseContext context;

        /**
         * Constructor
         *
         * @param element
         */
        public SimpleConfigNode(
                ArooaElement element,
                SimpleParseContext owner) {
            this.element = Objects.requireNonNull(element);
            this.context = Objects.requireNonNull(owner);
        }

        /**
         * Adds characters from #PCDATA areas to the wrapped element.
         *
         * @param data Text to add.
         *             Should not be <code>null</code>.
         */
        public void addText(String data) throws ArooaException {
            textHandler.addText(data);
        }

        public String getText() {
            return textHandler.getText();
        }

        @Override
        public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parseParentContext)
                throws ArooaParseException {

            ParseHandle<P> handle = parseParentContext.getElementHandler()
                    .onStartElement(element, parseParentContext);

            final P newContext = handle.getContext();

            if (textHandler.getText() != null) {
                handle.addText(textHandler.getText());
            }

            for (ConfigurationNode<SimpleParseContext> child : children()) {
                child.parse(newContext);
            }

            int index = handle.init();

            return new ChainingConfigurationHandle<>(
                    getContext(), parseParentContext, index);
        }

        public SimpleParseContext getContext() {
            return context;
        }

        @Override
        public String toString() {
            return "Simple ConfigurationNode for " + element;
        }
    }
}
