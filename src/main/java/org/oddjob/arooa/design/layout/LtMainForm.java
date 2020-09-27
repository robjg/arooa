package org.oddjob.arooa.design.layout;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.screem.*;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @oddjob.description A form for designing a component.
 */
public class LtMainForm implements DesignFactory {

    /**
     * @oddjob.property
     * @oddjob.description The form title. This isn't used at the moment.
     * @oddjob.required No.
     */
    private String title;

    /**
     * @oddjob.property
     * @oddjob.description The form items.
     * @oddjob.required No but pointless is missing.
     */
    private List<LtFormItem> formItems = new ArrayList<>();

    @Override
    public DesignInstance createDesign(ArooaElement element, ArooaContext parentContext) throws ArooaPropertyException {

        List<DesignProperty> designProperties = new ArrayList<>();
        List<FormItem> formItems = new ArrayList<>();

        DesignInstance instance;
        if (parentContext.getArooaType() == ArooaType.COMPONENT) {
            instance = new DesignComponentBase(element, parentContext) {

                @Override
                public DesignProperty[] children() {
                    return designProperties.stream().toArray(DesignProperty[]::new);
                }

                @Override
                public Form detail() {
                    return formFrom(this, title, formItems);
                }

            };
        }
        else {
            instance = new DesignValueBase(element, parentContext) {

                @Override
                public DesignProperty[] children() {
                    return designProperties.stream().toArray(DesignProperty[]::new);
                }

                @Override
                public Form detail() {
                    return formFrom(this, title, formItems);
                }
            };
        }

        LtFormItemVisitor visitor = new VisitorImpl(
                instance, designProperties::add, formItems::add);

        this.formItems.forEach(item -> item.accept(visitor));

        return instance;
    }

    static class VisitorImpl implements LtFormItemVisitor {

        private final DesignInstance instance;

        private final Consumer<DesignProperty> designPropertyConsumer;

        private final Consumer<FormItem> formItemConsumer;

        VisitorImpl(DesignInstance instance,
                    Consumer<DesignProperty> designPropertyConsumer,
                    Consumer<FormItem> formItemConsumer) {
            this.instance = instance;
            this.designPropertyConsumer = designPropertyConsumer;
            this.formItemConsumer = formItemConsumer;
        }

        @Override
        public void visit(LtTextField textField) {
            DesignAttributeProperty designProperty =
                    new SimpleTextAttribute(textField.getProperty(), instance);
            designPropertyConsumer.accept(designProperty);
            formItemConsumer.accept(designProperty.view().setTitle(textField.getTitle()));
        }

        @Override
        public void visit(LtTextArea textArea) {
            SimpleTextProperty designProperty = new SimpleTextProperty(null);
            designPropertyConsumer.accept(designProperty);
            formItemConsumer.accept(designProperty.view().setTitle(textArea.getTitle()));
        }

        @Override
        public void visit(LtFieldGroup fieldGroup) {
            GroupBase group;
            Consumer<FormItem> formItemConsumer;
            if ( fieldGroup.isBordered()) {
                BorderedGroup modelBorderedGroup = new BorderedGroup();
                formItemConsumer = modelBorderedGroup::add;
                group = modelBorderedGroup;
            }
            else {
                FieldGroup modelFieldGroup = new FieldGroup();
                formItemConsumer = modelFieldGroup::add;
                group = modelFieldGroup;
            }

            LtFormItemVisitor visitor = new VisitorImpl(
                    instance, designPropertyConsumer, formItemConsumer);

            fieldGroup.getFormItemsList().forEach(
                    item -> item.accept(visitor));

            group.setTitle(fieldGroup.getTitle());
            this.formItemConsumer.accept(group);
        }

        @Override
        public void visit(LtRadioSelection radioSelection) {
            FieldSelection modelFieldSelection = new FieldSelection();

            LtFormItemVisitor visitor = new VisitorImpl(
                    instance, designPropertyConsumer, modelFieldSelection::add);

            radioSelection.getFormItemsList().forEach(
                    item -> item.accept(visitor));

            modelFieldSelection.setTitle(radioSelection.getTitle());
            formItemConsumer.accept(modelFieldSelection);
        }

        @Override
        public void visit(LtTabGroup tabGroup) {
            TabGroup modelTabGroup = new TabGroup();

            LtFormItemVisitor visitor = new VisitorImpl(
                    instance, designPropertyConsumer, modelTabGroup::add);

            tabGroup.getFormItemsList().forEach(
                    item -> item.accept(visitor));

            modelTabGroup.setTitle(tabGroup.getTitle());
            formItemConsumer.accept(modelTabGroup);
        }

        @Override
        public void visit(LtIndexedTypeSelection indexedTypeSelection) {
            DesignElementProperty designProperty = new IndexedDesignProperty(
                    indexedTypeSelection.getProperty(), instance);

            designPropertyConsumer.accept(designProperty);
            formItemConsumer.accept(designProperty.view()
                    .setTitle(indexedTypeSelection.getTitle()));
        }

        @Override
        public void visit(LtMappedTypeSelection mappedTypeSelection) {
            DesignElementProperty designProperty = new MappedDesignProperty(
                    mappedTypeSelection.getProperty(), instance);

            designPropertyConsumer.accept(designProperty);
            formItemConsumer.accept(designProperty.view()
                    .setTitle(mappedTypeSelection.getTitle()));
        }

        @Override
        public void visit(LtSingleTypeSelection singleTypeSelection) {
            DesignElementProperty designProperty = new SimpleDesignProperty(
                    singleTypeSelection.getProperty(), instance);

            designPropertyConsumer.accept(designProperty);
            formItemConsumer.accept(designProperty.view()
                    .setTitle(singleTypeSelection.getTitle()));
        }
    }


    static Form formFrom(DesignInstance designInstance, String title, List<FormItem> formItems) {
        StandardForm form = new StandardForm(title, designInstance);
        formItems.forEach(form::addFormItem);
        return form;
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
