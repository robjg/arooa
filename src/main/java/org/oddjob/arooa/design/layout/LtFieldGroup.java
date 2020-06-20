package org.oddjob.arooa.design.layout;

import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @oddjob.description A group of form items.
 */
public class LtFieldGroup implements LtFormItem {

    /**
     * @oddjob.property
     * @oddjob.description The items in the group.
     * @oddjob.required No but pointless if missing.
     */
    private List<LtFormItem> formItems = new ArrayList<>();

    /**
     * @oddjob.property
     * @oddjob.description The title of the group. Will be used if the
     * group is in a tab or the group is bordered.
     * @oddjob.required No.
     */
    private String title;

    /**
     * @oddjob.property
     * @oddjob.description Is there a border round the group.
     * @oddjob.required No. Defaults to false.
     */
    private boolean bordered;

    @Override
    public void accept(LtFormItemVisitor visitor) {
        visitor.visit(this);
    }

    public void setFormItems(int index, LtFormItem formItem) {
        new ListSetterHelper(formItems).set(index, formItem);
    }

    public List<LtFormItem> getFormItemsList() {
        return formItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isBordered() {
        return bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }
}
