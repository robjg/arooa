package org.oddjob.arooa.parsing;

/**
 * The result of parsing an {@link ArooaElement} with an {@link ElementHandler} in
 * a given {@link ParseContext}.
 *
 * @param <P> The type of the Parse Context.
 */
public interface ParseHandle<P extends ParseContext<P>> {

    /**
     * A Context in which to parse child elements.
     *
     * @return
     */
    P getContext();

    /**
     * A mechanism  by which a parser may add text to the context.
     *
     * @param text
     */
    void addText(String text);

    /**
     * Initialise the context. To be used once all child elements have been
     * parsed.
     *
     * @return The position of this context relative to sibling contexts in
     * their parent.
     */
    int init();

}
