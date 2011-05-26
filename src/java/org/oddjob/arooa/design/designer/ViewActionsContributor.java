package org.oddjob.arooa.design.designer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.Unknown;
import org.oddjob.arooa.design.actions.AbstractArooaAction;
import org.oddjob.arooa.design.actions.ActionContributor;
import org.oddjob.arooa.design.actions.ActionMenu;
import org.oddjob.arooa.design.actions.ActionRegistry;
import org.oddjob.arooa.design.actions.ArooaAction;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ViewActionsContributor implements ActionContributor {

	private static final Logger logger = Logger.getLogger(ViewActionsContributor.class);
	
	public static final String VIEW_MENU_ID = "view";
	
	public static final String VIEW_GROUP = "view";

	private final ArooaAction viewAction = new AbstractArooaAction() {
		private static final long serialVersionUID = 2008121800L;

		{
			putValue(NAME, "Toggle View");
			
			putValue(ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));			
		}
		
		public void actionPerformed(ActionEvent e) {
			delegate.actionPerformed(e);
		}
	};

	private ActionListener delegate;
	
	public ViewActionsContributor(final DesignerModel model) {
		model.addPropertyChangeListener("currentComponent", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateActions(model);
			}
		});
		updateActions(model);
	}
	
	public void contributeTo(ActionRegistry actionRegistry) {
		
		actionRegistry.addMainMenu(
				new ActionMenu(
						VIEW_MENU_ID, 
						"View",
						KeyEvent.VK_V));
		
		actionRegistry.addMenuItem(VIEW_MENU_ID, VIEW_GROUP, viewAction);
		
		actionRegistry.addContextMenuItem(VIEW_GROUP, viewAction);
	}
	
	private void updateActions(final DesignerModel model) {

		DesignComponent currentComponent = model.getCurrentComponent();

		if (currentComponent == null) {
			viewAction.setEnabled(false);
		}
		else {
			viewAction.setEnabled(true);

			if (currentComponent instanceof Unknown) {
				
				viewAction.putValue(Action.NAME, "Component View");
				
				delegate = new ActionListener() {
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
				};
			}
			else {
				
				viewAction.putValue(Action.NAME, "XML View");
				
				delegate = new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						
						try {
							model.viewSelectedAsXML();
						} catch (Exception ex) {
							logger.error("Failed to view XML: " + ex.getMessage(), ex);
						}
					}
				};
			}
			
		}
	}
	
}
