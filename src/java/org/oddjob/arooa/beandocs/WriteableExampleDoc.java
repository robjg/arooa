package org.oddjob.arooa.beandocs;

public class WriteableExampleDoc implements ExampleDoc {

	private String firstSentence;
	
	private String allText;
	
	@Override
	public String getFirstSentence() {
		return firstSentence;
	}
	
	public void setFirstSentence(String firstLine) {
		this.firstSentence = firstLine;
	}
	
	@Override
	public String getAllText() {
		return allText;
	}
	
	public void setAllText(String allText) {
		this.allText = allText;
	}
}
