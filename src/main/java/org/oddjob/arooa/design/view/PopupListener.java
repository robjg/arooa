/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 *
 */
class PopupListener extends MouseAdapter {
	private JPopupMenu popup;
	
	public void setPopup(JPopupMenu popup) {
		this.popup = popup;
	}
	
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}
	public void mouseClicked(MouseEvent e) {
		maybeShowPopup(e);
	}
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
		if (popup != null && e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
