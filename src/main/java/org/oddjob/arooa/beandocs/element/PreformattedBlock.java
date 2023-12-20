package org.oddjob.arooa.beandocs.element;

/**
 * Wraps a block of plain text.
 */
public class PreformattedBlock implements BeanDocElement {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public <C, R> R accept(ElementVisitor<C, R> visitor, C context) {
        return visitor.visitPreformattedBlock(this, context);
    }
}
