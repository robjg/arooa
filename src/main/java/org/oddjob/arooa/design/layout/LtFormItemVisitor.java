package org.oddjob.arooa.design.layout;

/**
 * Visitor an {@link LtFormItem}.
 */
public interface LtFormItemVisitor {

    void visit(LtTextField textField);

    void visit(LtTextArea textArea);

    void visit(LtFieldGroup fieldGroup);

    void visit(LtRadioSelection radioSelection);

    void visit(LtTabGroup tabGroup);

    void visit(LtSingleTypeSelection singleTypeSelection);

    void visit(LtIndexedTypeSelection indexedTypeSelection);

    void visit(LtMappedTypeSelection mappedTypeSelection);
}


