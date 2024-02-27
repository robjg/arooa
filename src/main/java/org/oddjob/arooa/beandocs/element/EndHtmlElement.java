package org.oddjob.arooa.beandocs.element;

/**
 * An End Html Element. The text will include the markup. The name will be just the name of the tag.
 */
public class EndHtmlElement implements BeanDocElement {

    private String text;

    private String name;

    public static EndHtmlElement of(String name, String text) {
        EndHtmlElement endHtmlElement = new EndHtmlElement();
        endHtmlElement.setName(name);
        endHtmlElement.setText(text);

        return endHtmlElement;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <C, R> R accept(DocElementVisitor<C, R> visitor, C context) {
        return visitor.visitEndHtmlElement(this, context);
    }
}
