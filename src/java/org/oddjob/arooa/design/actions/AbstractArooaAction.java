package org.oddjob.arooa.design.actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;

abstract public class AbstractArooaAction extends AbstractAction implements ArooaAction {
	private static final long serialVersionUID = 2009080700L;
	
    /**
     * Specifies whether action is visible; the default is true.
     */
    private boolean visible = true;

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public AbstractArooaAction() {
    }
    
    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public AbstractArooaAction(String name) {
    	super(name);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public AbstractArooaAction(String name, Icon icon) {
		super(name, icon);
    }
    
    /**
     * Returns true if the action is visible.
     *
     * @return true if the action is visible, false otherwise
     * @see ArooaAction#isVisible.
     */
    public boolean isVisible() {
    	return visible;
    }

    /**
     * Make the action visible or invisible.
     *
     * @param newValue  true to make the action visible, false to
     *                  make it invisible.
     * @see ArooaAction#setVisible
     */
    public void setVisible(boolean newValue) {
    	boolean oldValue = this.visible;

    	if (oldValue != newValue) {
    		this.visible = newValue;
    		firePropertyChange(VISIBLE_PROPERTY, 
    				Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
    	}
    }

}
