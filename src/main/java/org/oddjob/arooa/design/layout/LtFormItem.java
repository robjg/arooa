package org.oddjob.arooa.design.layout;

/**
 * Something that can appear on a form.
 */
public interface LtFormItem {

    /**
     * The visitor pattern.
     *
     * @param visitor The visitor.
     */
    void accept(LtFormItemVisitor visitor);
}
