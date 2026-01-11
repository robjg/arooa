package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

/**
 * Provide documentation on a conversion.
 */
public interface ConversionDoc {

    /**
     * The type or method that provides the documentation.
     *
     * @return Type or method name. Should not be null.
     */
    String getTypeOrMethod();

    /**
     * The 'from' type of the conversion.
     *
     * @return The type name. Should not be null.
     */
    String getFromType();

    /**
     * The 'to' type of the conversion.
     *
     * @return The type name. Will be null for a Joker.
     */
    String getToType();

    /**
     * The first sentence of the description.
     *
     * @return List of Bean Doc Elements. Maybe be empty but never null.
     */
    List<BeanDocElement> getFirstSentence();

    /**
     * All the description including the first sentence.
     *
     * @return List of Bean Doc Elements. Maybe be empty but never null.
     */
    List<BeanDocElement> getAllText();


}
