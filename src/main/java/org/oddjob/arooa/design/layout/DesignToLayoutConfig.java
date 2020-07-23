package org.oddjob.arooa.design.layout;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.screem.*;
import org.oddjob.arooa.json.ConfigurationTree;
import org.oddjob.arooa.json.ConfigurationTreeBuilder;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.parsing.QTag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Convert any Design into a Layout Design Configuration.
 */
public class DesignToLayoutConfig {

    public static final String DESIGN_FORM = "design:form";
    public static final String DESIGN_TEXT = "design:text";
    public static final String DESIGN_TEXTAREA = "design:textarea";
    public static final String DESIGN_SINGLE = "design:single";
    public static final String DESIGN_GROUP = "design:group";
    public static final String DESIGN_TABS = "design:tabs";
    public static final String DESIGN_RADIO = "design:radio";
    public static final String DESIGN_INDEXED = "design:indexed";
    public static final String DESIGN_MAPPED = "design:mapped";

    public static final String TITLE = "title";
    public static final String FORM_ITEMS = "formItems";
    public static final String PROPERTY = "property";
    public static final String BORDERED = "bordered";

    private final ArooaSession session;

    private final NamespaceMappings namespaceMappings;

    public DesignToLayoutConfig(ArooaSession session) {
        this.session = session;
        this.namespaceMappings = session.getArooaDescriptor();
    }

    public DesignableInfo configurationFor(DesignFactory designFactory, ArooaElement element) {

        Objects.requireNonNull(designFactory);
        Objects.requireNonNull(element);

        ArooaContext context = new DesignSeedContext(ArooaType.COMPONENT, session);

        DesignInstance instance = designFactory.createDesign(element, context);

        Form form = instance.detail();

        if (form instanceof StandardForm) {
            Map<String, String[]> options = new HashMap<>();
            ArooaConfiguration configuration = parse((StandardForm) form, options)
                    .toConfiguration((c) -> {});

            return new DesignableInfo(configuration, options);
        }
        else {
            throw new UnsupportedOperationException("Can't parse " +
                    form.getClass().getName());
        }
    }

    ConfigurationTree parse(StandardForm standardForm, Map<String, String[]> options) {

        ConfigurationTreeBuilder.WithQualifiedTag treeBuilder = ConfigurationTreeBuilder
                .ofNamespaceMappings(namespaceMappings)
                .withTags();

        treeBuilder.setTag(DESIGN_FORM);
        treeBuilder.addAttribute(TITLE, standardForm.getTitle());

        int numFormItems = standardForm.size();

        for (int i= 0; i < numFormItems; ++i) {
            FormItem item = standardForm.getFormItem(i);
            treeBuilder.addChild(FORM_ITEMS, parse(item, options));
        }

        return treeBuilder.build();
    }

    ConfigurationTree parse(FormItem item, Map<String, String[]> options) {

        ConfigurationTreeBuilder.WithQualifiedTag treeBuilder = ConfigurationTreeBuilder
                .ofNamespaceMappings(namespaceMappings)
                .withTags();

        if (item instanceof TextField) {
            TextField textField = (TextField) item;

            treeBuilder.setTag(DESIGN_TEXT);
            treeBuilder.addAttribute(PROPERTY, textField.getAttribute().property());
            treeBuilder.addAttribute(TITLE, textField.getTitle());
        }
        else if (item instanceof TextInput) {
            TextInput textInput = (TextInput) item;

            treeBuilder.setTag(DESIGN_TEXTAREA);
            treeBuilder.addAttribute(TITLE, textInput.getTitle());
        }
        else if (item instanceof FileSelection) {
            FileSelection fileSelection = (FileSelection) item;

            treeBuilder.setTag(DESIGN_TEXT);
            treeBuilder.addAttribute(PROPERTY, fileSelection.getAttribute().property());
            treeBuilder.addAttribute(TITLE, fileSelection.getTitle());
        }
        else if (item instanceof FieldSelection) {
            FieldSelection fieldSelection = (FieldSelection) item;

            treeBuilder.setTag(DESIGN_RADIO);
            treeBuilder.addAttribute(TITLE, fieldSelection.getTitle());
            for (int i= 0; i < fieldSelection.size(); ++i) {
                FormItem child = fieldSelection.get(i);
                treeBuilder.addChild(FORM_ITEMS, parse(child, options));
            }
        }
        else if (item instanceof FieldGroup) {
            FieldGroup fieldGroup = (FieldGroup) item;

            treeBuilder.setTag(DESIGN_GROUP);
            treeBuilder.addAttribute(TITLE, fieldGroup.getTitle());
            for (int i= 0; i < fieldGroup.size(); ++i) {
                FormItem child = fieldGroup.get(i);
                treeBuilder.addChild(FORM_ITEMS, parse(child, options));
            }
            if (item instanceof BorderedGroup) {
                treeBuilder.addAttribute(BORDERED, "true");
            }
        }
        else if (item instanceof TabGroup) {
            TabGroup tabGroup = (TabGroup) item;

            treeBuilder.setTag(DESIGN_TABS);
            treeBuilder.addAttribute(TITLE, tabGroup.getTitle());
            for (int i= 0; i < tabGroup.size(); ++i) {
                FormItem child = tabGroup.get(i);
                treeBuilder.addChild(FORM_ITEMS, parse(child, options));
            }
        }
        else if (item instanceof SingleTypeSelection) {

            SingleTypeSelection singleTypeSelection = (SingleTypeSelection) item;
            DesignElementProperty designProperty = singleTypeSelection.getDesignElementProperty();

            options.put(designProperty.property(),supportedTypes(designProperty));

            treeBuilder.setTag(DESIGN_SINGLE);
            treeBuilder.addAttribute(TITLE, singleTypeSelection.getTitle());
            treeBuilder.addAttribute(PROPERTY, designProperty.property());
        }
        else if (item instanceof MultiTypeTable) {

            MultiTypeTable multiTypeTable = (MultiTypeTable) item;
            DesignElementProperty designProperty = multiTypeTable.getDesignProperty();

            options.put(designProperty.property(),supportedTypes(designProperty));

            if (multiTypeTable.isKeyed()) {
                treeBuilder.setTag(DESIGN_MAPPED);
            }
            else {
                treeBuilder.setTag(DESIGN_INDEXED);
            }

            treeBuilder.addAttribute(TITLE, multiTypeTable.getTitle());
            treeBuilder.addAttribute(PROPERTY, designProperty.property());
        }
        else {
            throw new IllegalArgumentException("Can't parse " + item.getClass().getName());
        }

        return treeBuilder.build();
    }

    String[] supportedTypes(DesignElementProperty designProperty) {
        QTag[] supportedTypes = new InstanceSupport(designProperty).getTags();
        return Arrays.stream(supportedTypes)
                .map(QTag::toString)
                .toArray(String[]::new);
    }
}
