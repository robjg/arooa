package org.oddjob.arooa.design.actions;

import javax.swing.Action;

/**
 * An Action that can be made invisible. This is so that menu
 * options can only appear when the action is visible.
 * 
 * @author rob
 *
 */
public interface ArooaAction extends Action {

	public static final String VISIBLE_PROPERTY = "visible";
	
    /**
     * Sets the visible state of the <code>Action</code>.  When enabled,
     * any component associated with this object is visible and
     * able to fire this object's <code>actionPerformed</code> method.
     * If the value has changed, a <code>PropertyChangeEvent</code> is sent
     * to listeners.
     *
     * @param  b true to make this <code>Action</code> visible, false to 
     * make it invisible.
     */
    public void setVisible(boolean b);
    
    /**
     * Returns the visible state of the <code>Action</code>. When visible,
     * any component associated with this object is active and
     * able to fire this object's <code>actionPerformed</code> method.
     *
     * @return true if this <code>Action</code> is visible.
     */
	public boolean isVisible();
}
