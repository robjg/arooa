package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

/**
 * A Conversion Doc bean that can be written to.
 */
public class WriteableConversionDoc implements ConversionDoc {

    private String typeOrMethod;

    private String fromType;

    private String toType;

    private List<BeanDocElement> firstSentence;
	
	private List<BeanDocElement> allText;

    @Override
    public String getTypeOrMethod() {
        return typeOrMethod;
    }

    public void setTypeOrMethod(String typeOrMethod) {
        this.typeOrMethod = typeOrMethod;
    }

    @Override
    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    @Override
    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    @Override
	public List<BeanDocElement> getFirstSentence() {
		return firstSentence;
	}

	public void setFirstSentence(List<BeanDocElement> firstLine) {
		this.firstSentence = firstLine;
	}
	
	@Override
	public List<BeanDocElement> getAllText() {
		return allText;
	}

	public void setAllText(List<BeanDocElement> allText) {
		this.allText = allText;
	}

}
