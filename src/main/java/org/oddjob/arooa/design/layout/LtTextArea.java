package org.oddjob.arooa.design.layout;

/**
 * @oddjob.description A text area for a text property. The can be only one
 * of these in a form.
 */
public class LtTextArea implements LtFormItem {

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
