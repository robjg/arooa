package org.oddjob.arooa.design.screem;

public class LabelledComboBox<T> implements FormItem {

	private String title;
	
	private T selected;
	
	private final T[] selections;
	
	public LabelledComboBox(T[] selections) {
		this.selections = selections;
	}
	
	public LabelledComboBox(T[] selections, String title) {
		this.selections = selections;
		this.title = title;
	}
	
	@Override
	public FormItem setTitle(String title) {
		this.title = title;
		return this;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	public T getSelected() {
		return selected;
	}
	
	public void setSelected(T selected) {
		this.selected = selected;
	}
	
	@Override
	public boolean isPopulated() {
		return selected != null;
	}
	
	public T[] getSelections() {
		return selections;
	}
}
