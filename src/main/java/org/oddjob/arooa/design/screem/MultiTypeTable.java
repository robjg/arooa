/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.screem;

import org.oddjob.arooa.design.DesignElementProperty;
import org.oddjob.arooa.design.view.Looks;

/**
 * A model for a DesignElement which can contain
 * multiple child DesignElements of various types.
 * This model supports both name types, as used in a Map
 * or unnamed types as used in a List.
 * <p>
 */
public class MultiTypeTable implements FormItem {
	
	private String title;
	private final DesignElementProperty designProperty;

	private KeyAccess keyAccess;
	
	private int visibleRows = Looks.LIST_ROWS;
	
	public MultiTypeTable(DesignElementProperty designProperty) {
		this(designProperty.property(), designProperty);
	}

	public MultiTypeTable(String heading, DesignElementProperty designProperty) {
		this.title = heading;
		this.designProperty = designProperty;
	}

	public MultiTypeTable setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public boolean isKeyed() {
		return !(keyAccess == null);
	}
	
	public void setKeyAccess(KeyAccess keyAccess) {
		this.keyAccess = keyAccess;
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.model.DesignDefinition#isPopulated()
	 */
	public boolean isPopulated() {
		return designProperty.isPopulated();
	}

	public DesignElementProperty getDesignProperty() {
		return designProperty;
	}
	
//	public int childCount() {
//		return designProperty.instanceCount();
//	}
//	
	public String getChildName(int index) {
		return keyAccess.getKey(index);
	}
	
	public void setChildName(int index, String key) {
		keyAccess.setKey(index, key);
	}

//	public DesignInstance getChildValue(int index) {
//		return designProperty.instanceAt(index);
//	}
//	
	/**
	 * @return Returns the visibleRows.
	 */
	public int getVisibleRows() {
		return visibleRows;
	}
	/**
	 * @param visibleRows The visibleRows to set.
	 */
	public MultiTypeTable setVisibleRows(int visibleRows) {
		this.visibleRows = visibleRows;
		return this;
	}
	
	public interface KeyAccess {
		void setKey(int index, String value);
		String getKey(int index);
	}
	
}
