/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.oddjob.arooa.design.designer.DesignerModel;
import org.oddjob.arooa.design.view.Standards;


public class ViewXMLAction extends AbstractAction {
	private static final long serialVersionUID = 20080417;
	
	private final DesignerModel model;
	
	public ViewXMLAction(DesignerModel model) {
		this.model = model;
		putValue(Action.NAME, "View XML");
		putValue(Action.MNEMONIC_KEY, Standards.VIEW_XML_MNEMONIC_KEY); 
		putValue(Action.ACCELERATOR_KEY, Standards.VIEW_XML_ACCELERATOR_KEY);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {		
		
		try {
			model.viewSelectedAsXML();
		} catch (Exception ex) {
			ex.printStackTrace();
			
			// TODO: Error Box.
		}
	}	
}