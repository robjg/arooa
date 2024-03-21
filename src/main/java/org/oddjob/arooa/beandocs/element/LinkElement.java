package org.oddjob.arooa.beandocs.element;

/**
 * A link to either another reference item or java doc. Processed
 * from the {@link LinkElement#setPropertyName(String) A link with a property}
 */
public class LinkElement implements BeanDocElement {

    /** The label if any after the actual link specification. In the above
     * it would be 'A link with a property.' */
    private String label;

    /** The link ref as it appears in the tag. In the above it would be
     * @{code LinkElement#setPropertyName(String)} */
    private String signature;

    /** The qualified type of the Link. In the above it would be
     * {@code org.oddjob.arooa.beandocs.element.LinkElement}. */
    private String qualifiedType;

    /** The derived property name if it's possible from the field or getter or setter.
     * In the above example it would {@code propertyName}. */
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
