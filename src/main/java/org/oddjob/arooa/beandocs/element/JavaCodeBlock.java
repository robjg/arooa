package org.oddjob.arooa.beandocs.element;

/**
 * Wraps a block of Java code.
 */
public class JavaCodeBlock implements BeanDocElement {

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public <C, R> R accept(ElementVisitor<C, R> visitor, C context) {
        return visitor.visitJavaCodeBlock(this, context);
    }
}
