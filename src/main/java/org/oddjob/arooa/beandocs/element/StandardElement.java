package org.oddjob.arooa.beandocs.element;

/**
 * Standard element that has no special formatting
 */
public class StandardElement implements BeanDocElement {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public <C, R> R accept(ElementVisitor<C, R> visitor, C context) {
        return visitor.visitStandard(this, context);
    }
}
