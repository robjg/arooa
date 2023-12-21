package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WriteableBeanDoc implements BeanDoc {

	private String prefix;
	
	private String tag;
	
	private String className;
	
	private List<BeanDocElement> firstSentence;
	
	private List<BeanDocElement> allText;
	
	private final Map<String, WriteablePropertyDoc> propertyDocs =
            new TreeMap<>();
	
	private final List<ExampleDoc> exampleDocs =
            new ArrayList<>();
	
	@Override
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String namespace) {
		this.prefix = namespace;
	}
	
	@Override
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String getName() {
		String prefix = "";
		if (this.prefix != null) {
			prefix = this.prefix + ":";
		}
		return prefix + tag;
	}
	
	@Override
	public String getClassName() {
		return this.className;
	}
	
	public void setClassName(String className) {
		this.className = className;
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

	@Override
	public WriteablePropertyDoc[] getPropertyDocs() {
		return propertyDocs.values().toArray(new WriteablePropertyDoc[0]);
	}

	@Override
	public WriteablePropertyDoc propertyDocFor(String property) {
		return propertyDocs.get(property);
	}
	
	public void addPropertyDoc(WriteablePropertyDoc propertyDocBean) {
		propertyDocs.put(propertyDocBean.getPropertyName(),
				propertyDocBean);
	}
	
	@Override
	public ExampleDoc[] getExampleDocs() {
		return exampleDocs.toArray(new ExampleDoc[0]);
	}
	
	public void addExampleDoc(WriteableExampleDoc exampleDoc) {
		exampleDocs.add(exampleDoc);
	}
}
