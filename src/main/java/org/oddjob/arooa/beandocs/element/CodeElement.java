package org.oddjob.arooa.beandocs.element;

/**
 * Inline preformatted element.
 */
public class CodeElement implements BeanDocElement {

    private String text;

    public static CodeElement of(String text) {
        CodeElement literalElement = new CodeElement();
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
    public <C, R> R accept(DocElementVisitor<C, R> visitor, C context) {
        return visitor.visitCode(this, context);
    }
}
