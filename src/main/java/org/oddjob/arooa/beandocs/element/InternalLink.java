package org.oddjob.arooa.beandocs.element;

/**
 * A link to either another reference item or java doc.
 */
public class InternalLink implements BeanDocElement {

    private String name;

    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public <C, R> R accept(ElementVisitor<C, R> visitor, C context) {
        return visitor.visitInternalLink(this, context);
    }
}
