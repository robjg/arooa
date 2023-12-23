package org.oddjob.arooa.beandocs.element;

/**
 * Inline literal that needs to be escaped when formatted to HTML.
 */
public class LiteralElement implements BeanDocElement {

    private String text;

    public static LiteralElement of(String text) {
        LiteralElement literalElement = new LiteralElement();
        literalElement.setText(text);

        return literalElement;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public <C, R> R accept(ElementVisitor<C, R> visitor, C context) {
        return visitor.visitLiteral(this, context);
    }
}
