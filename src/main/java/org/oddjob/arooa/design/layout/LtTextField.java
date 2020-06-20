package org.oddjob.arooa.design.layout;

/**
 * @oddjob.description A text field.
 */
public class LtTextField implements LtFormItem {

    /**
     * @oddjob.property
     * @oddjob.description The property name.
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
