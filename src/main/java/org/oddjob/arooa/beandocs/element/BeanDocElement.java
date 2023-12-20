package org.oddjob.arooa.beandocs.element;

/**
 * A part of a section of Documentation.
 */
public interface BeanDocElement {

    /**
     * Accept a visitor to the element which will be visited with this element.
     *
     * @param visitor The visitor.
     * @param context Some context to be passed to the visitor.
     *
     * @return The result from the visitor.
     *
     * @param <C> The type of the context.
     * @param <R> The type of the result.
     */
    <C, R> R accept(ElementVisitor<C, R> visitor, C context);
}
