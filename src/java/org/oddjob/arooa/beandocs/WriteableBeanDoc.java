package org.oddjob.arooa.beandocs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WriteableBeanDoc implements BeanDoc {

	private String prefix;
	
	private String tag;
	
	private String className;
	
	private String firstSentence;
	
	private String allText;
	
	private Map<String, WriteablePropertyDoc> propertyDocs =
		new TreeMap<String, WriteablePropertyDoc>();
	
	private List<ExampleDoc> exampleDocs =
		new ArrayList<ExampleDoc>();
	
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

	@Override
	public WriteablePropertyDoc[] getPropertyDocs() {
		return propertyDocs.values().toArray(
				new WriteablePropertyDoc[propertyDocs.size()]);
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
		return exampleDocs.toArray(new ExampleDoc[exampleDocs.size()]);
	}
	
	public void addExampleDoc(WriteableExampleDoc exampleDoc) {
		exampleDocs.add(exampleDoc);
	}
}
