package org.oddjob.arooa.forms;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.screem.*;
import org.oddjob.arooa.json.ConfigurationTree;
import org.oddjob.arooa.json.ConfigurationTreeBuilder;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.parsing.QTag;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Convert any Design Instance into a Form Configuration.
 */
public class DesignToFormConfig {

    public static final String FORMS_FORM = "forms:form";
    public static final String FORMS_BEAN_FORM = "forms:bean-form";
    public static final String FORMS_TEXT = "forms:text";
    public static final String FORMS_INLINE_TEXT = "forms:inline-text";
    public static final String FORMS_TEXT_AREA = "forms:text-area";
    public static final String FORMS_SINGLE = "forms:single";
    public static final String FORMS_GROUP = "forms:group";
    public static final String FORMS_TABS = "forms:tabs";
    public static final String FORMS_RADIO = "forms:radio";
    public static final String FORMS_INDEXED = "forms:indexed";
    public static final String FORMS_MAPPED = "forms:mapped";

    public static final String ID_PROP = "id";
    public static final String ID_TITLE = "Id";
    public static final String TITLE = "title";
    public static final String ELEMENT = "element";
    public static final String COMPONENT = "component";
    public static final String PROPERTY_CLASS = "propertyClass";
    public static final String INVALID_CLASS = "invalidClass";
    public static final String FOR_CLASS = "forClass";
    public static final String FORM_ITEMS = "formItems";
    public static final String PROPERTY = "property";
    public static final String BORDERED = "bordered";
    public static final String VALUE = "value";
    public static final String OPTIONS = "options";

    private final NamespaceMappings formsPrefixMappings;

    public DesignToFormConfig() {
        this.formsPrefixMappings = FormsLookup.formsNamespaces();
    }

    public ArooaConfiguration configurationFor(DesignInstance instance) {

        return parse(instance).toConfiguration((c) -> {
        });
    }

    ConfigurationTree parse(DesignInstance instance) {
        Form form = instance.detail();

        ConfigurationTreeBuilder.WithQualifiedTag treeBuilder = ConfigurationTreeBuilder
                .ofNamespaceMappings(formsPrefixMappings)
                .withTags();

        treeBuilder.addAttribute(ELEMENT,
                new QTag(instance.element(), instance.getArooaContext()).toString());

        if (form instanceof StandardForm) {
            treeBuilder.setTag(FORMS_FORM);
            return parse((StandardForm) form, treeBuilder);
        } else if (form instanceof TextPseudoForm) {
            treeBuilder.setTag(FORMS_INLINE_TEXT);
            return parse((TextPseudoForm) form, treeBuilder);
        } else if (form instanceof BeanForm) {
            treeBuilder.setTag(FORMS_BEAN_FORM);
            return parse((BeanForm) form, treeBuilder);
        } else if (form instanceof NullForm) {
            treeBuilder.setTag(FORMS_FORM);
            return treeBuilder.build();
        } else {
            throw new UnsupportedOperationException("Can't parse " +
                    form.getClass().getName() + " from " + instance.getClass().getName());
        }
    }

    ConfigurationTree parse(TextPseudoForm textPseudoForm,
                            ConfigurationTreeBuilder.WithQualifiedTag treeBuilder) {

        treeBuilder.addAttribute(VALUE, textPseudoForm.getAttribute().attribute());

        return treeBuilder.build();
    }

    private void addAttribute(ConfigurationTreeBuilder.WithQualifiedTag treeBuilder, String id) {

        ConfigurationTreeBuilder.WithQualifiedTag childBuilder = treeBuilder.newInstance();

        childBuilder.setTag(FORMS_TEXT);
        childBuilder.addAttribute(PROPERTY, ID_PROP);
        childBuilder.addAttribute(TITLE, ID_TITLE);
        childBuilder.addAttribute(VALUE, id);

        treeBuilder.addChild(FORM_ITEMS, childBuilder.build());
    }

    ConfigurationTree parse(StandardForm standardForm,
                            ConfigurationTreeBuilder.WithQualifiedTag treeBuilder) {

        treeBuilder.addAttribute(TITLE, standardForm.getTitle());

        if (standardForm.getDesign() instanceof DesignComponent) {
            addAttribute(treeBuilder, ((DesignComponent) standardForm.getDesign()).getId());
        }

        int numFormItems = standardForm.size();

        for (int i = 0; i < numFormItems; ++i) {
            FormItem item = standardForm.getFormItem(i);
            treeBuilder.addChild(FORM_ITEMS, parse(item));
        }

        return treeBuilder.build();
    }

    ConfigurationTree parse(BeanForm beanForm,
                            ConfigurationTreeBuilder.WithQualifiedTag treeBuilder) {

        DynamicDesignInstance instance = beanForm.getDesign();
        Optional.ofNullable(instance.getClassName()).ifPresent(
                cn -> treeBuilder.addAttribute(FOR_CLASS, cn));

        treeBuilder.addAttribute(TITLE, beanForm.getTitle());

        if (beanForm.getDesign() instanceof DesignComponent) {
            addAttribute(treeBuilder, ((DesignComponent) beanForm.getDesign()).getId());
        }

        Form subForm = beanForm.getSubForm();

        if (subForm instanceof BeanForm.PropertiesForm) {

            BeanForm.PropertiesForm propertiesForm = (BeanForm.PropertiesForm) subForm;

            int numFormItems = propertiesForm.size();

            for (int i = 0; i < numFormItems; ++i) {
                FormItem item = propertiesForm.getFormItem(i);
                treeBuilder.addChild(FORM_ITEMS, parse(item));
            }
        } else if (subForm instanceof BeanForm.ClassNotFoundForm) {
            treeBuilder.addAttribute(INVALID_CLASS, "true");
        }

        return treeBuilder.build();
    }

    ConfigurationTree parse(FormItem item) {

        ConfigurationTreeBuilder.WithQualifiedTag treeBuilder = ConfigurationTreeBuilder
                .ofNamespaceMappings(formsPrefixMappings)
                .withTags();

        if (item instanceof TextField) {
            TextField textField = (TextField) item;

            treeBuilder.setTag(FORMS_TEXT);
            treeBuilder.addAttribute(PROPERTY, textField.getAttribute().property());
            treeBuilder.addAttribute(TITLE, textField.getTitle());
            treeBuilder.addAttribute(VALUE, textField.getAttribute().attribute());
        } else if (item instanceof TextInput) {
            TextInput textInput = (TextInput) item;

            treeBuilder.setTag(FORMS_TEXT_AREA);
            treeBuilder.addAttribute(TITLE, textInput.getTitle());
            treeBuilder.addAttribute(VALUE, textInput.getText());
        } else if (item instanceof FileSelection) {
            FileSelection fileSelection = (FileSelection) item;

            treeBuilder.setTag(FORMS_TEXT);
            treeBuilder.addAttribute(PROPERTY, fileSelection.getAttribute().property());
            treeBuilder.addAttribute(TITLE, fileSelection.getTitle());
            treeBuilder.addAttribute(VALUE, fileSelection.getAttribute().attribute());
        } else if (item instanceof FieldSelection) {
            FieldSelection fieldSelection = (FieldSelection) item;

            treeBuilder.setTag(FORMS_RADIO);
            treeBuilder.addAttribute(TITLE, fieldSelection.getTitle());
            for (int i = 0; i < fieldSelection.size(); ++i) {
                FormItem child = fieldSelection.get(i);
                treeBuilder.addChild(FORM_ITEMS, parse(child));
            }
        } else if (item instanceof FieldGroup) {
            FieldGroup fieldGroup = (FieldGroup) item;

            treeBuilder.setTag(FORMS_GROUP);
            treeBuilder.addAttribute(TITLE, fieldGroup.getTitle());
            for (int i = 0; i < fieldGroup.size(); ++i) {
                FormItem child = fieldGroup.get(i);
                treeBuilder.addChild(FORM_ITEMS, parse(child));
            }
            if (item instanceof BorderedGroup) {
                treeBuilder.addAttribute(BORDERED, "true");
            }
        } else if (item instanceof TabGroup) {
            TabGroup tabGroup = (TabGroup) item;

            treeBuilder.setTag(FORMS_TABS);
            treeBuilder.addAttribute(TITLE, tabGroup.getTitle());
            for (int i = 0; i < tabGroup.size(); ++i) {
                FormItem child = tabGroup.get(i);
                treeBuilder.addChild(FORM_ITEMS, parse(child));
            }
        } else if (item instanceof SingleTypeSelection) {

            SingleTypeSelection singleTypeSelection = (SingleTypeSelection) item;
            DesignElementProperty designProperty = singleTypeSelection.getDesignElementProperty();

            treeBuilder.setTag(FORMS_SINGLE);
            treeBuilder.addAttribute(TITLE, singleTypeSelection.getTitle());
            treeBuilder.addAttribute(PROPERTY, designProperty.property());
            treeBuilder.addAttribute(OPTIONS, supportedTypes(designProperty));
            treeBuilder.addAttribute(PROPERTY_CLASS,
                    designProperty.getArooaContext().getRuntime()
                            .getClassIdentifier().forClass().getName());
            if (designProperty.getArooaContext().getArooaType() == ArooaType.COMPONENT) {
                    treeBuilder.addAttribute(COMPONENT, "true");
            }

            designProperty.addDesignListener(new DesignListener() {
                @Override
                public void childAdded(DesignStructureEvent event) {
                    treeBuilder.addChild(VALUE, parse(event.getChild()));
                }

                @Override
                public void childRemoved(DesignStructureEvent event) {
                }
            });
        } else if (item instanceof MultiTypeTable) {

            MultiTypeTable multiTypeTable = (MultiTypeTable) item;
            DesignElementProperty designProperty = multiTypeTable.getDesignProperty();

            if (multiTypeTable.isKeyed()) {
                treeBuilder.setTag(FORMS_MAPPED);
            } else {
                treeBuilder.setTag(FORMS_INDEXED);
            }

            treeBuilder.addAttribute(TITLE, multiTypeTable.getTitle());
            treeBuilder.addAttribute(PROPERTY, designProperty.property());
            treeBuilder.addAttribute(OPTIONS, supportedTypes(designProperty));
            treeBuilder.addAttribute(PROPERTY_CLASS,
                    designProperty.getArooaContext().getRuntime()
                            .getClassIdentifier().forClass().getName());
            if (designProperty.getArooaContext().getArooaType() == ArooaType.COMPONENT) {
                treeBuilder.addAttribute(COMPONENT, "true");
            }

            designProperty.addDesignListener(new DesignListener() {
                @Override
                public void childAdded(DesignStructureEvent event) {
                    treeBuilder.addChild(VALUE, parse(event.getChild()));
                }

                @Override
                public void childRemoved(DesignStructureEvent event) {
                }
            });

        } else {
            throw new IllegalArgumentException("Can't parse " + item.getClass().getName());
        }

        return treeBuilder.build();
    }

    String supportedTypes(DesignElementProperty designProperty) {
        QTag[] supportedTypes = new InstanceSupport(designProperty).getTags();
        return Arrays.stream(supportedTypes)
                .map(QTag::toString)
                .collect(Collectors.joining(","));
    }
}
