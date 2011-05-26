/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.designer.DesignerModel;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.design.view.Standards;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;


public class ViewComponentsAction extends AbstractAction {
	private static final long serialVersionUID = 2008111300;
	
	private static final Logger logger = Logger.getLogger(ViewComponentsAction.class);
	
	private final DesignerModel model;
		
	public ViewComponentsAction(DesignerModel model) {

		this.model = model;
		
		putValue(Action.NAME, "View Components");
		putValue(Action.MNEMONIC_KEY, Standards.VIEW_COMP_MNEMONIC_KEY); 
		putValue(Action.ACCELERATOR_KEY, Standards.VIEW_COMP_ACCELERATOR_KEY);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		UnknownInstance unknown = (UnknownInstance) model.getCurrentSelection().getDesignComponent();
		String xml = unknown.getXml();

		// check the xml.
		try {
			new XMLArooaParser().parse(
					unknown.getArooaContext().getConfigurationNode());
		} catch (ArooaParseException ex) {
		
			logger.error("Failed to parse XML: " + ex.getMessage(),
					ex);
			
			return;
		}
		
		try {
			model.replaceSelected(new XMLConfiguration("XML", xml));
		} catch (ArooaParseException ex) {
			
			logger.error("Failed to Create Component Tree: " + ex.getMessage(),
					ex);			
		}
		
	}	
}