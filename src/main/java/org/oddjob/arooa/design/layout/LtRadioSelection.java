package org.oddjob.arooa.design.layout;

import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @oddjob.description A radio button selection of form items.
 */
public class LtRadioSelection implements LtFormItem {

    /**
     * @oddjob.property
     * @oddjob.description The form items to choose from. Can be single fields or
     * groups.
     * @oddjob.required No but pointless if missing.
     */
    private List<LtFormItem> formItems = new ArrayList<>();

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
}
