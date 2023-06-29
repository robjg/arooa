package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.TextHandler;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.AutoSetter;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;

import java.util.Objects;

/**
 * Holds configuration information for an instance of something
 * (a component or value).
 *
 * @see ComponentConfiguration
 * @see ObjectConfiguration
 */
abstract class InstanceConfiguration {

    private final ArooaClass arooaClass;

    /**
     * The object to configure.
     */
    private final Object wrappedObject;

    private final TextHandler textHandler = new TextHandler();

    private final AutoSetter autoSetter;

    /** Provided by subclasses to control when to set the wrapped object
     * into the parent. For a component it is on init, for a value it is
     * on configure. */
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
     * @param arooaClass    The type of the wrapped object.
     * @param wrappedObject The object to configure and set on the parent.
     *                      Must not be <code>null</code>.
     */
    InstanceConfiguration(ArooaClass arooaClass,
                          Object wrappedObject) {

        Objects.requireNonNull(arooaClass, "No ArooaClass.");
        Objects.requireNonNull(wrappedObject , "No Wrapped Object.");

        this.arooaClass = arooaClass;
        this.wrappedObject = wrappedObject;

        this.autoSetter = new AutoSetter();
    }

    /**
     * Allow subclasses to do something when the context becomes available.
     * Here to allow the {@link ComponentConfiguration} to register a component.
     *
     * @param context The new context
     */
    void contextAvailable(ArooaContext context) {
    }

    /**
     * Provide attributes that we might have modified during parsing (such
     * as id if it was a duplicate)
     *
     * @return The attributes.
     */
    ArooaAttributes getAttributes() {
        return getAttributeSetter().getAttributes();
    }

    Object getWrappedObject() {
        return wrappedObject;
    }

    ArooaClass getArooaClass() {
        return arooaClass;
    }

    abstract Object getObjectToSet();

    /**
     * Implemented by subclasses to alter attribute setting behaviour.
     *
     * @return An AttributeSetter. Never null.
     */
    abstract AttributeSetter getAttributeSetter();

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

        String textProperty = beanDescriptor.getTextProperty();

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

        getAttributeSetter().init(context);

        String text = getText();
        if (text != null) {
            ArooaSession session = context.getSession();

            ExpressionParser expressionParser = session.getTools().getExpressionParser();
            ParsedExpression evaluator = expressionParser.parse(text);

            if (evaluator.isConstant()) {
                setTextProperty(text, session);
            }
        }

    }

    final void internalConfigure(ArooaContext context)
            throws ArooaConfigurationException {

        // configure attributes
        getAttributeSetter().configure(context);

        String text = getText();
        if (text != null) {

            ArooaSession session = context.getSession();

            ExpressionParser expressionParser = session.getTools().getExpressionParser();
            ParsedExpression evaluator = expressionParser.parse(text);

            if (!evaluator.isConstant()) {

                try {
                    String replacement = evaluator.evaluate(
                            session, String.class);

                    setTextProperty(replacement,
                                    session);
                } catch (ArooaConversionException e) {
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
