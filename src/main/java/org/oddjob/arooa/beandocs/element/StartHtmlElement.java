package org.oddjob.arooa.beandocs.element;

/**
 * A Start Html Element. The text will include the markup. The name will be just the name of the tag.
 */
public class StartHtmlElement implements BeanDocElement {

    private String text;

    private String name;

    public static StartHtmlElement of(String name, String text) {
        StartHtmlElement startHtmlElement = new StartHtmlElement();
        startHtmlElement.setName(name);
        startHtmlElement.setText(text);

        return startHtmlElement;
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
        return visitor.visitStartHtmlElement(this, context);
    }

}
