/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.designer;

import java.awt.Component;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The Swing component that contains the design form for the currently
 * selected component, or is blank.
 * 
 * @author rob
 */
public class DesignerDetail extends JScrollPane implements Observer {
	private static final long serialVersionUID = 2008112100L;
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		DesignerModel model = (DesignerModel) o;
		DesignTreeNode node = model.getCurrentSelection();
		Component component = null;
		if (node != null) {
			component = node.getDetailView();
		}
		
		if (component != null) {
			setViewportView(component);
		}
		else {
			setViewportView(new JPanel());
		}
	}
	
}
