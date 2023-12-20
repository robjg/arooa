package org.oddjob.arooa.beandocs.element;

/**
 * Wraps a block of XML.
 */
public class XmlBlock implements BeanDocElement {

    private String xml;

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public <C, R> R accept(ElementVisitor<C, R> visitor, C context) {
        return visitor.visitXmlBlock(this, context);
    }
}
