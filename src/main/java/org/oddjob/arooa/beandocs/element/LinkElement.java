package org.oddjob.arooa.beandocs.element;

/**
 * A link to either another reference item or java doc.
 */
public class LinkElement implements BeanDocElement {

    private String label;

    private String signature;

    private String qualifiedType;

    private String propertyName;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getQualifiedType() {
        return qualifiedType;
    }

    public void setQualifiedType(String qualifiedType) {
        this.qualifiedType = qualifiedType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public <C, R> R accept(DocElementVisitor<C, R> visitor, C context) {
        return visitor.visitInternalLink(this, context);
    }
}
