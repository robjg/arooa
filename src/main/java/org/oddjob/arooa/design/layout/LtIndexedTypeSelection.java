package org.oddjob.arooa.design.layout;

/**
 * @oddjob.description A Form Item for an indexed property.
 */
public class LtIndexedTypeSelection implements LtFormItem {

    /**
     * @oddjob.property
     * @oddjob.description The name of the property
     * @oddjob.required Yes.
     */
    private String property;

    /**
     * @oddjob.property
     * @oddjob.description An optional title.
     * @oddjob.required No.
     */
    private String title;

    @Override
    public void accept(LtFormItemVisitor visitor) {
        visitor.visit(this);
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
