package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

public class WriteableExampleDoc implements ExampleDoc {

	private List<BeanDocElement> firstSentence;
	
	private List<BeanDocElement> allText;
	
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
