/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.TextInput;

/**
 * @author Rob Gordon.
 */
public class SimpleTextProperty  
implements DesignTextProperty {

	private String text;

	private final String property;
	
	public SimpleTextProperty(String property) {
		this.property = property;
	}
	
	public String property() {
		return property;
	}

	public String text() {
		return text;
	}
	
	
	public void text(String text) {
		if ("".equals(text)) {
			this.text = null;
		}
		else {
			this.text = text;
		}
	}
	
	
	public final FormItem view() {
		return new TextInput(this.property,
				new TextInput.TextSource() {
			public String getText() {
				return text();
			}
			public void setText(String text) {
				text(text);
			}
		});
	}
	
	public boolean isPopulated() {
		return text != null && text.length() > 0;
	}
}
