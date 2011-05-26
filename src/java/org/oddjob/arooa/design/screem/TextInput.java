/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;




/**
 * A model for a visual component which is intended to be rendered as a
 * large text area.
 * <p>
 * The text is retrieved and set via a text exchange object.
 */
public final class TextInput 
implements FormItem, Form {

	private String heading;
	
	private TextSource textSource;
	
	public TextInput(String heading, TextSource textSource) {
		this.heading = heading;
		this.textSource = textSource;
	}

	public FormItem setTitle(String title) {
		this.heading = title;
		return this;
	}
	
	public String getTitle() {
		return heading;
	}

	public void setText(String text) {
		textSource.setText(text);
	}

	public String getText() {
		return textSource.getText();
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.model.DesignDefinition#isPopulated()
	 */
	public boolean isPopulated() {
		return getText() != null && getText().length() > 0;
	}
	
	public interface TextSource {
		
		public void setText(String text);

		public String getText();
		
	}
}
